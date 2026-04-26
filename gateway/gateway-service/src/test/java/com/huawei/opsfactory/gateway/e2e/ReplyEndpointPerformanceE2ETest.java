package com.huawei.opsfactory.gateway.e2e;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.hook.HookContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReplyEndpointPerformanceE2ETest extends BaseE2ETest {

    private ManagedInstance mockInstance;

    @Before
    public void setUp() throws Exception {
        mockInstance = new ManagedInstance("test-agent", "alice", 9999, 12345L, null, "test-secret");
        mockInstance.setStatus(ManagedInstance.Status.RUNNING);
        when(agentConfigService.getUserAgentDir(anyString(), anyString()))
                .thenReturn(Paths.get(System.getProperty("java.io.tmpdir")));
        when(fileService.listFiles(any()))
                .thenReturn(Collections.emptyList());
        when(fileService.diffFiles(any(), any()))
                .thenReturn(Collections.emptyList());
    }

    @Test
    public void reply_responseLatencyIncludesHookAndSpawnDelay() {
        when(hookPipeline.executeRequest(any(HookContext.class)))
                .thenAnswer(invocation -> Mono.delay(Duration.ofMillis(80))
                        .thenReturn(((HookContext) invocation.getArgument(0)).getBody()));
        when(instanceManager.getOrSpawn("test-agent", "alice"))
                .thenReturn(Mono.delay(Duration.ofMillis(90)).thenReturn(mockInstance));
        when(sseRelayService.relay(eq(9999), eq("/reply"), anyString(), eq("test-agent"), eq("alice"), anyString()))
                .thenReturn(Flux.just(sseChunk("hook-spawn")));

        long elapsedMs = executeReplyAndMeasure("{\"message\":\"hello\"}");

        assertTrue("reply latency should include hook + spawn delays, actual=" + elapsedMs,
                elapsedMs >= 140);
        assertTrue("reply latency should stay within a reasonable bound, actual=" + elapsedMs,
                elapsedMs < 5000);
    }

    @Test
    public void reply_responseLatencyIncludesResumeDelay() {
        String body = "{\"session_id\":\"session-123\",\"message\":\"hello\"}";

        when(hookPipeline.executeRequest(any(HookContext.class)))
                .thenAnswer(invocation -> Mono.just(((HookContext) invocation.getArgument(0)).getBody()));
        when(instanceManager.getOrSpawn("test-agent", "alice"))
                .thenReturn(Mono.just(mockInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(org.springframework.http.HttpMethod.POST), eq("/agent/resume"),
                anyString(), anyInt(), anyString()))
                .thenReturn(Mono.delay(Duration.ofMillis(120)).thenReturn("{\"ok\":true}"));
        when(sseRelayService.relay(eq(9999), eq("/reply"), anyString(), eq("test-agent"), eq("alice"), anyString()))
                .thenReturn(Flux.just(sseChunk("resume")));

        long elapsedMs = executeReplyAndMeasure(body);

        verify(goosedProxy).fetchJson(eq(9999), eq(org.springframework.http.HttpMethod.POST), eq("/agent/resume"),
                anyString(), anyInt(), anyString());
        assertTrue("reply latency should include resume delay, actual=" + elapsedMs,
                elapsedMs >= 100);
        assertTrue("reply latency should stay within a reasonable bound, actual=" + elapsedMs,
                elapsedMs < 5000);
    }

    @Test
    public void reply_responseLatencyIncludesUpstreamFirstChunkDelay() {
        when(hookPipeline.executeRequest(any(HookContext.class)))
                .thenAnswer(invocation -> Mono.just(((HookContext) invocation.getArgument(0)).getBody()));
        when(instanceManager.getOrSpawn("test-agent", "alice"))
                .thenReturn(Mono.just(mockInstance));
        when(sseRelayService.relay(eq(9999), eq("/reply"), anyString(), eq("test-agent"), eq("alice"), anyString()))
                .thenReturn(Flux.just(sseChunk("upstream")).delaySubscription(Duration.ofMillis(150)));

        long elapsedMs = executeReplyAndMeasure("{\"message\":\"hello\"}");

        assertTrue("reply latency should include upstream first chunk delay, actual=" + elapsedMs,
                elapsedMs >= 130);
        assertTrue("reply latency should stay within a reasonable bound, actual=" + elapsedMs,
                elapsedMs < 5000);
    }

    private long executeReplyAndMeasure(String body) {
        long startNs = System.nanoTime();
        String responseBody = webClient.post().uri("/gateway/agents/test-agent/reply")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        long elapsedMs = Duration.ofNanos(System.nanoTime() - startNs).toMillis();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("data:"));
        return elapsedMs;
    }

    private DataBuffer sseChunk(String content) {
        return new DefaultDataBufferFactory()
                .wrap(("data: {\"content\":\"" + content + "\"}\n\n").getBytes(StandardCharsets.UTF_8));
    }
}
