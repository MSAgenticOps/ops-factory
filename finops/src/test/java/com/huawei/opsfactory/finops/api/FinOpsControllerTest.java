package com.huawei.opsfactory.finops.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.opsfactory.finops.model.FinOpsModels.SessionMessageRecord;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionUsageRecord;
import com.huawei.opsfactory.finops.store.FinOpsSnapshotStore;
import com.huawei.opsfactory.finops.store.SessionDbReader;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "finops.secret-key=test-secret",
        "finops.data-root=target/missing-finops-test-data",
        "finops.scan.refresh-interval-ms=3600000"
    }
)
class FinOpsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FinOpsSnapshotStore snapshotStore;

    @Test
    void rejectsRequestsWithoutConfiguredSecret() {
        ResponseEntity<String> response = restTemplate.getForEntity("/finops/overview", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void exposesOverviewWithConfiguredSecret() {
        snapshotStore.update(new SessionDbReader.ScanResult(List.of(session("session-1")), List.of(), 1, 0, "test", null));

        ResponseEntity<String> response = restTemplate.exchange(
            "/finops/overview?compare=true",
            HttpMethod.GET,
            new HttpEntity<>(headers()),
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"snapshotStatus\"");
        assertThat(response.getBody()).contains("\"summary\"");
        assertThat(response.getBody()).contains("\"taskExecutionLoad\"");
        assertThat(response.getBody()).contains("\"topAgents\"");
        assertThat(response.getBody()).doesNotContain("\"recommendations\"");
        assertThat(response.getBody()).doesNotContain("workingDir");
        assertThat(response.getBody()).doesNotContain("threadId");
        assertThat(response.getBody()).doesNotContain("modelConfig");
        assertThat(response.getBody()).doesNotContain("recipe");
        assertThat(response.getBody()).doesNotContain("/tmp/internal-workdir");
    }

    @Test
    void exposesPaginatedListsWithPublicSessionFieldsOnly() {
        snapshotStore.update(new SessionDbReader.ScanResult(List.of(session("session-1"), session("session-2")), List.of(), 1, 0, "test", null));

        ResponseEntity<String> response = getWithSecret("/finops/sessions?page=1&size=1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"page\":1");
        assertThat(response.getBody()).contains("\"size\":1");
        assertThat(response.getBody()).contains("\"totalItems\":2");
        assertThat(response.getBody()).contains("\"totalPages\":2");
        assertThat(response.getBody()).contains("\"label\"");
        assertThat(response.getBody()).doesNotContain("workingDir");
        assertThat(response.getBody()).doesNotContain("threadId");
        assertThat(response.getBody()).doesNotContain("modelConfig");
        assertThat(response.getBody()).doesNotContain("recipe");
    }

    @Test
    void rejectsMalformedTimestampAsBadRequest() {
        ResponseEntity<String> response = getWithSecret("/finops/overview?startTime=bad-time");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("FINOPS_INVALID_REQUEST");
    }

    @Test
    void exposesSessionMessagesForUserAgentScopedSession() {
        Instant now = Instant.now();
        snapshotStore.update(new SessionDbReader.ScanResult(
            List.of(session("session-1")),
            List.of(new SessionMessageRecord(
                "session-1",
                "admin",
                "qa-agent",
                "message-1",
                1,
                "user",
                now,
                now,
                null,
                24,
                "Open the large report",
                "Open the large report",
                false,
                false,
                true,
                "read_file",
                false,
                true,
                true
            )),
            1,
            0,
            "test",
            null
        ));

        ResponseEntity<String> response = getWithSecret("/finops/sessions/session-1/messages?userId=admin&agentId=qa-agent");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"session\"");
        assertThat(response.getBody()).contains("\"messages\"");
        assertThat(response.getBody()).contains("\"messageTokenAvailable\":false");
        assertThat(response.getBody()).contains("\"toolSignalAvailable\":true");
        assertThat(response.getBody()).contains("\"toolResponse\":true");
        assertThat(response.getBody()).contains("Open the large report");
        assertThat(response.getBody()).doesNotContain("workingDir");
        assertThat(response.getBody()).doesNotContain("threadId");
    }

    @Test
    void removedRecommendationAndReportEndpointsStayUnavailable() {
        assertThat(getWithSecret("/finops/recommendations").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getWithSecret("/finops/reports/summary").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<String> getWithSecret(String path) {
        return restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(headers()), String.class);
    }

    private static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-secret-key", "test-secret");
        return headers;
    }

    private static SessionUsageRecord session(String id) {
        Instant updatedAt = Instant.now().minusSeconds(60);
        return new SessionUsageRecord(
            id,
            "admin",
            "qa-agent",
            "Session " + id,
            "user",
            "/tmp/internal-workdir",
            updatedAt.minusSeconds(3600),
            updatedAt,
            1000,
            900,
            100,
            1000,
            900,
            100,
            null,
            "custom_provider",
            "qwen/test",
            "auto",
            "thread-" + id,
            3,
            1,
            1,
            1,
            "Session " + id,
            Map.of("secret", "raw-model-config"),
            Map.of("prompt", "raw-recipe")
        );
    }
}
