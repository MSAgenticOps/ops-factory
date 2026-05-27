package com.huawei.opsfactory.finops.api;

import com.huawei.opsfactory.finops.model.FinOpsModels.AgentUsage;
import com.huawei.opsfactory.finops.model.FinOpsModels.ModelUsage;
import com.huawei.opsfactory.finops.model.FinOpsModels.OverviewResponse;
import com.huawei.opsfactory.finops.model.FinOpsModels.PageResponse;
import com.huawei.opsfactory.finops.model.FinOpsModels.QueryFilter;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionMessageCapabilities;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionMessageDetail;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionMessageRecord;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionMessageStats;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionMessagesResponse;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionUsage;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionUsageRecord;
import com.huawei.opsfactory.finops.model.FinOpsModels.SnapshotStatus;
import com.huawei.opsfactory.finops.model.FinOpsModels.UserUsage;
import com.huawei.opsfactory.finops.service.UsageAggregationService;
import com.huawei.opsfactory.finops.service.UsageIngestionService;
import com.huawei.opsfactory.finops.store.FinOpsSnapshotStore;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finops")
public class FinOpsController {

    private final FinOpsSnapshotStore snapshotStore;
    private final UsageIngestionService ingestionService;
    private final UsageAggregationService aggregationService;

    public FinOpsController(FinOpsSnapshotStore snapshotStore,
                            UsageIngestionService ingestionService,
                            UsageAggregationService aggregationService) {
        this.snapshotStore = snapshotStore;
        this.ingestionService = ingestionService;
        this.aggregationService = aggregationService;
    }

    @GetMapping("/overview")
    public OverviewResponse overview(@RequestParam(value = "startTime", required = false) String startTime,
                                     @RequestParam(value = "endTime", required = false) String endTime,
                                     @RequestParam(value = "agentId", required = false) String agentId,
                                     @RequestParam(value = "userId", required = false) String userId,
                                     @RequestParam(value = "sessionType", required = false) String sessionType,
                                     @RequestParam(value = "providerName", required = false) String providerName,
                                     @RequestParam(value = "modelName", required = false) String modelName,
                                     @RequestParam(value = "compare", required = false) Boolean compare) {
        FinOpsSnapshotStore.Snapshot snapshot = snapshotStore.current();
        QueryFilter filter = filter(startTime, endTime, agentId, userId, sessionType, providerName, modelName, compare);
        List<SessionUsageRecord> current = aggregationService.filterCurrent(snapshot.sessions(), filter);
        List<SessionUsageRecord> previous = aggregationService.filterPrevious(snapshot.sessions(), filter);
        return new OverviewResponse(
            snapshot.status(),
            aggregationService.comparison(current, previous),
            aggregationService.tokenTrend(current),
            aggregationService.agents(current).stream().limit(10).toList(),
            aggregationService.users(current).stream().limit(10).toList(),
            aggregationService.topSessions(current, 10).stream().map(FinOpsController::toSessionUsage).toList(),
            aggregationService.models(current).stream().limit(10).toList(),
            aggregationService.taskExecutionLoad(current),
            aggregationService.distribution(current, SessionUsageRecord::sessionType),
            aggregationService.distribution(current, SessionUsageRecord::providerName)
        );
    }

    @GetMapping("/agents")
    public PageResponse<AgentUsage> agents(@RequestParam(value = "startTime", required = false) String startTime,
                                           @RequestParam(value = "endTime", required = false) String endTime,
                                           @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", required = false, defaultValue = "25") Integer size) {
        var snapshot = snapshotStore.current();
        var filter = filter(startTime, endTime, null, null, null, null, null, false);
        return page(snapshot.status(), aggregationService.agents(aggregationService.filterCurrent(snapshot.sessions(), filter)), page, size);
    }

    @GetMapping("/agents/{agentId}")
    public PageResponse<SessionUsage> agent(@PathVariable("agentId") String agentId,
                                            @RequestParam(value = "startTime", required = false) String startTime,
                                            @RequestParam(value = "endTime", required = false) String endTime,
                                            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                            @RequestParam(value = "size", required = false, defaultValue = "25") Integer size) {
        var snapshot = snapshotStore.current();
        var filter = filter(startTime, endTime, agentId, null, null, null, null, false);
        var sessions = aggregationService.topSessions(aggregationService.filterCurrent(snapshot.sessions(), filter), Integer.MAX_VALUE).stream()
            .map(FinOpsController::toSessionUsage)
            .toList();
        return page(snapshot.status(), sessions, page, size);
    }

    @GetMapping("/users")
    public PageResponse<UserUsage> users(@RequestParam(value = "startTime", required = false) String startTime,
                                         @RequestParam(value = "endTime", required = false) String endTime,
                                         @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", required = false, defaultValue = "25") Integer size) {
        var snapshot = snapshotStore.current();
        var filter = filter(startTime, endTime, null, null, null, null, null, false);
        return page(snapshot.status(), aggregationService.users(aggregationService.filterCurrent(snapshot.sessions(), filter)), page, size);
    }

    @GetMapping("/users/{userId}")
    public PageResponse<SessionUsage> user(@PathVariable("userId") String userId,
                                           @RequestParam(value = "startTime", required = false) String startTime,
                                           @RequestParam(value = "endTime", required = false) String endTime,
                                           @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", required = false, defaultValue = "25") Integer size) {
        var snapshot = snapshotStore.current();
        var filter = filter(startTime, endTime, null, userId, null, null, null, false);
        var sessions = aggregationService.topSessions(aggregationService.filterCurrent(snapshot.sessions(), filter), Integer.MAX_VALUE).stream()
            .map(FinOpsController::toSessionUsage)
            .toList();
        return page(snapshot.status(), sessions, page, size);
    }

    @GetMapping("/sessions")
    public PageResponse<SessionUsage> sessions(@RequestParam(value = "startTime", required = false) String startTime,
                                               @RequestParam(value = "endTime", required = false) String endTime,
                                               @RequestParam(value = "agentId", required = false) String agentId,
                                               @RequestParam(value = "userId", required = false) String userId,
                                               @RequestParam(value = "sessionType", required = false) String sessionType,
                                               @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                               @RequestParam(value = "size", required = false, defaultValue = "25") Integer size) {
        var snapshot = snapshotStore.current();
        var filter = filter(startTime, endTime, agentId, userId, sessionType, null, null, false);
        var sessions = aggregationService.topSessions(aggregationService.filterCurrent(snapshot.sessions(), filter), Integer.MAX_VALUE).stream()
            .map(FinOpsController::toSessionUsage)
            .toList();
        return page(snapshot.status(), sessions, page, size);
    }

    @GetMapping("/sessions/{sessionId}")
    public SessionUsage session(@PathVariable("sessionId") String sessionId) {
        return snapshotStore.current().sessions().stream()
            .filter(session -> sessionId.equals(session.id()))
            .map(FinOpsController::toSessionUsage)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public SessionMessagesResponse sessionMessages(@PathVariable("sessionId") String sessionId,
                                                   @RequestParam("userId") String userId,
                                                   @RequestParam("agentId") String agentId) {
        var snapshot = snapshotStore.current();
        SessionUsageRecord session = snapshot.sessions().stream()
            .filter(item -> sessionId.equals(item.id()) && userId.equals(item.userId()) && agentId.equals(item.agentId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        List<SessionMessageRecord> messages = snapshot.messages().stream()
            .filter(item -> sessionId.equals(item.sessionId()) && userId.equals(item.userId()) && agentId.equals(item.agentId()))
            .toList();
        return new SessionMessagesResponse(
            snapshot.status(),
            toSessionUsage(session),
            toMessageStats(messages),
            toCapabilities(messages),
            messages.stream().map(FinOpsController::toMessageDetail).toList()
        );
    }

    @GetMapping("/models")
    public PageResponse<ModelUsage> models(@RequestParam(value = "startTime", required = false) String startTime,
                                           @RequestParam(value = "endTime", required = false) String endTime,
                                           @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", required = false, defaultValue = "25") Integer size) {
        var snapshot = snapshotStore.current();
        var filter = filter(startTime, endTime, null, null, null, null, null, false);
        return page(snapshot.status(), aggregationService.models(aggregationService.filterCurrent(snapshot.sessions(), filter)), page, size);
    }

    @PostMapping("/refresh")
    public Object refresh() {
        return ingestionService.refresh().status();
    }

    private QueryFilter filter(String startTime, String endTime, String agentId, String userId, String sessionType,
                               String providerName, String modelName, Boolean compare) {
        return aggregationService.buildFilter(startTime, endTime, agentId, userId, sessionType, providerName, modelName, compare);
    }

    private static SessionUsage toSessionUsage(SessionUsageRecord session) {
        return new SessionUsage(
            session.id(),
            session.userId(),
            session.agentId(),
            session.name(),
            session.sessionType(),
            session.createdAt(),
            session.updatedAt(),
            session.totalTokens(),
            session.inputTokens(),
            session.outputTokens(),
            session.scheduleId(),
            session.providerName(),
            session.modelName(),
            session.messageCount(),
            session.userMessageCount(),
            session.assistantMessageCount(),
            session.toolResponseCount(),
            session.label()
        );
    }

    private static SessionMessageDetail toMessageDetail(SessionMessageRecord message) {
        return new SessionMessageDetail(
            message.messageId(),
            message.rowId(),
            message.role(),
            message.createdAt(),
            message.insertedAt(),
            message.tokens(),
            message.contentLength(),
            message.contentPreview(),
            message.contentText(),
            message.contentTruncated(),
            message.toolRequest(),
            message.toolResponse(),
            message.toolName(),
            message.error(),
            message.userVisible(),
            message.agentVisible()
        );
    }

    private static SessionMessageStats toMessageStats(List<SessionMessageRecord> messages) {
        SessionMessageRecord largest = messages.stream()
            .max((left, right) -> Integer.compare(left.contentLength(), right.contentLength()))
            .orElse(null);
        return new SessionMessageStats(
            messages.size(),
            (int) messages.stream().filter(item -> "user".equalsIgnoreCase(item.role())).count(),
            (int) messages.stream().filter(item -> "assistant".equalsIgnoreCase(item.role())).count(),
            (int) messages.stream().filter(SessionMessageRecord::toolRequest).count(),
            (int) messages.stream().filter(SessionMessageRecord::toolResponse).count(),
            (int) messages.stream().filter(item -> item.tokens() != null).count(),
            largest == null ? 0 : largest.contentLength(),
            largest == null ? null : messageRole(largest),
            largest == null ? null : largest.contentPreview()
        );
    }

    private static SessionMessageCapabilities toCapabilities(List<SessionMessageRecord> messages) {
        return new SessionMessageCapabilities(
            messages.stream().anyMatch(item -> item.tokens() != null),
            messages.stream().anyMatch(item -> item.contentPreview() != null && !item.contentPreview().isBlank()),
            messages.stream().anyMatch(item -> item.toolRequest() || item.toolResponse())
        );
    }

    private static String messageRole(SessionMessageRecord message) {
        return message.toolRequest() || message.toolResponse() ? "tool" : message.role();
    }

    private static <T> PageResponse<T> page(SnapshotStatus status, List<T> items, Integer page, Integer size) {
        int safeSize = Math.max(1, Math.min(100, size == null ? 25 : size));
        int totalPages = Math.max(1, (int) Math.ceil((double) items.size() / safeSize));
        int safePage = Math.min(Math.max(1, page == null ? 1 : page), totalPages);
        int from = Math.min((safePage - 1) * safeSize, items.size());
        int to = Math.min(from + safeSize, items.size());
        return new PageResponse<>(status, items.subList(from, to), safePage, safeSize, items.size(), totalPages);
    }
}
