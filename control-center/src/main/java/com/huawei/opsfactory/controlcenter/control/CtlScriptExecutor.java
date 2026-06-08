/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.controlcenter.control;

import com.huawei.opsfactory.controlcenter.config.ControlCenterProperties;
import com.huawei.opsfactory.controlcenter.model.ServiceActionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

@Component
/**
 * Ctl Script Executor.
 *
 * @author x00000000
 * @since 2026-05-27
 */
public class CtlScriptExecutor {

    private static final String ANSI_PATTERN = "\\u001B\\[[;\\d]*m";

    private final ControlCenterProperties properties;

    /**
     * Creates the ctl script executor instance.
     *
     * @param properties the control center properties
     */
    @Autowired
    public CtlScriptExecutor(ControlCenterProperties properties) {
        this.properties = properties;
    }

    /**
     * Executes a ctl.sh script action for the given service.
     *
     * @param serviceId the service identifier
     * @param actionLabel the human-readable action label
     * @param ctlAction the ctl.sh action (e.g., "startup", "shutdown")
     * @param ctlComponent the ctl.sh component (e.g., "gateway", "knowledge-service")
     * @return the service action result
     */
    public ServiceActionResult execute(String serviceId, String actionLabel, String ctlAction, String ctlComponent) {
        long startedAt = System.currentTimeMillis();
        try {
            Path projectRoot = resolveProjectRoot();
            Path script = projectRoot.resolve("scripts").resolve("ctl.sh");
            ProcessBuilder builder = new ProcessBuilder(List.of(script.toString(), ctlAction, ctlComponent));
            builder.directory(projectRoot.toFile());
            builder.redirectErrorStream(true);
            Process process = builder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!output.isEmpty()) output.append('\n');
                    output.append(line);
                }
            }

            int exitCode = process.waitFor();
            long finishedAt = System.currentTimeMillis();
            String sanitized = output.toString().replaceAll(ANSI_PATTERN, "");
            return new ServiceActionResult(
                    serviceId,
                    actionLabel,
                    exitCode == 0,
                    startedAt,
                    finishedAt,
                    exitCode,
                    sanitized
            );
        } catch (Exception e) {
            long finishedAt = System.currentTimeMillis();
            return new ServiceActionResult(
                    serviceId,
                    actionLabel,
                    false,
                    startedAt,
                    finishedAt,
                    -1,
                    e.getMessage()
            );
        }
    }

    /**
     * Resolves the project root directory from configuration.
     *
     * @return the absolute path to the project root directory
     */
    private Path resolveProjectRoot() {
        String configuredRoot = properties.getProjectRoot();
        if (configuredRoot != null && !configuredRoot.isEmpty()) {
            Path configured = Path.of(configuredRoot);
            if (configured.isAbsolute()) {
                return configured.normalize();
            }
            return configured.toAbsolutePath().normalize();
        }
        return Path.of("").toAbsolutePath().normalize();
    }
}
