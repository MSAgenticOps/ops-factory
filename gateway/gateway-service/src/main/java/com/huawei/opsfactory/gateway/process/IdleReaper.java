package com.huawei.opsfactory.gateway.process;

import com.huawei.opsfactory.gateway.common.constants.GatewayConstants;
import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class IdleReaper {

    private static final Logger log = LogManager.getLogger(IdleReaper.class);

    private final InstanceManager instanceManager;
    private final GatewayProperties properties;
    private final PrewarmService prewarmService;

    public IdleReaper(InstanceManager instanceManager, GatewayProperties properties,
                      PrewarmService prewarmService) {
        this.instanceManager = instanceManager;
        this.properties = properties;
        this.prewarmService = prewarmService;
    }

    @Scheduled(fixedDelayString = "${gateway.idle.check-interval-ms:60000}")
    public void reapIdleInstances() {
        long maxIdleMs = properties.getIdle().getTimeoutMinutes() * 60_000L;
        long now = System.currentTimeMillis();
        Set<String> reapedUsers = new HashSet<>();

        for (ManagedInstance instance : instanceManager.getAllInstances()) {
            // Never reap sys instances
            if (GatewayConstants.SYS_USER.equals(instance.getUserId())) {
                continue;
            }
            if (instance.getStatus() == ManagedInstance.Status.RUNNING
                    && now - instance.getLastActivity() > maxIdleMs) {
                log.info("Reaping idle instance {}:{} (idle {}s)",
                        instance.getAgentId(), instance.getUserId(),
                        (now - instance.getLastActivity()) / 1000);
                instanceManager.stopInstance(instance);
                reapedUsers.add(instance.getUserId());
            }
        }

        // Reset pre-warm state for users who have no remaining instances
        for (String userId : reapedUsers) {
            boolean hasRemaining = instanceManager.getAllInstances().stream()
                    .anyMatch(i -> i.getUserId().equals(userId)
                            && i.getStatus() == ManagedInstance.Status.RUNNING);
            if (!hasRemaining) {
                prewarmService.clearUser(userId);
            }
        }
    }
}
