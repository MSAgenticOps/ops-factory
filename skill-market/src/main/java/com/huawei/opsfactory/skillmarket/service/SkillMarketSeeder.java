package com.huawei.opsfactory.skillmarket.service;

import com.huawei.opsfactory.skillmarket.config.SkillMarketProperties;
import com.huawei.opsfactory.skillmarket.model.CreateSkillRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SkillMarketSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SkillMarketSeeder.class);
    private static final String SEED_MARKER = ".seeded-ops-skills";

    private final SkillCatalogService catalogService;
    private final SkillMarketProperties properties;

    public SkillMarketSeeder(SkillCatalogService catalogService, SkillMarketProperties properties) {
        this.catalogService = catalogService;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Path markerPath = seedMarkerPath();
        int seeded = 0;
        int failed = 0;
        for (SeedSkill skill : seedSkills()) {
            try {
                catalogService.createSkillIfAbsent(new CreateSkillRequest(
                    skill.id(),
                    skill.name(),
                    skill.description(),
                    skill.markdown()
                ));
                seeded++;
            } catch (IOException | RuntimeException e) {
                failed++;
                log.warn("Failed to initialize seed skill id={} reason={}", skill.id(), e.getMessage());
            }
        }
        if (failed == 0) {
            Files.createDirectories(markerPath.getParent());
            Files.writeString(markerPath, "initialized\n");
        }
        log.info("skill-market seed skills ready count={} failed={} marker={}", seeded, failed, markerPath);
    }

    private Path seedMarkerPath() {
        return Path.of(properties.getRuntime().getBaseDir()).toAbsolutePath().normalize().resolve(SEED_MARKER);
    }

    private List<SeedSkill> seedSkills() {
        return List.of(
            new SeedSkill(
                "incident-triage",
                "Incident Triage",
                "Triage production incidents by severity, impact, timeline, and immediate containment actions.",
                """
                ---
                name: incident-triage
                description: Triage production incidents by severity, impact, timeline, and immediate containment actions.
                ---

                # Incident Triage

                ## When to Use

                Use this skill when the user reports an active incident, outage, degraded service, or major alert.

                ## Required Inputs

                - Affected service, region, tenant, or host
                - Alert name and first-seen time
                - User impact or error symptoms
                - Recent deploys, changes, or infrastructure events

                ## Workflow

                1. Classify severity from impact, scope, and duration.
                2. Build a short incident timeline.
                3. Identify the safest containment action.
                4. Separate confirmed facts from hypotheses.
                5. Recommend owner handoff and next checkpoints.

                ## Output Format

                ### Severity
                ### Impact
                ### Timeline
                ### Current Hypotheses
                ### Immediate Actions
                ### Follow-up Checks

                ## Rules

                - Do not speculate beyond available evidence.
                - Prefer containment over root-cause certainty during active impact.
                - Call out missing data explicitly.
                """
            ),
            new SeedSkill(
                "log-root-cause-analysis",
                "Log Root Cause Analysis",
                "Analyze logs to identify errors, exceptions, recurring patterns, and likely root causes.",
                """
                ---
                name: log-root-cause-analysis
                description: Analyze logs to identify errors, exceptions, recurring patterns, and likely root causes.
                ---

                # Log Root Cause Analysis

                ## When to Use

                Use this skill when the user provides application, middleware, OS, or gateway logs for troubleshooting.

                ## Required Inputs

                - Log file or pasted log content
                - Approximate incident time range
                - Service or component name
                - Expected behavior and observed symptom

                ## Workflow

                1. Detect timestamp, level, trace id, and component patterns.
                2. Extract ERROR, FATAL, exception, timeout, and retry records.
                3. Group repeated failures by signature.
                4. Build a timeline around first failure and escalation.
                5. Identify likely root cause with supporting log lines.

                ## Output Format

                ### Log Coverage
                ### Key Errors
                ### Timeline
                ### Root Cause Candidates
                ### Recommended Actions

                ## Rules

                - Quote only short log snippets needed as evidence.
                - Do not invent missing log content.
                - If logs are too large, state the analyzed range.
                """
            ),
            new SeedSkill(
                "kubernetes-pod-recovery",
                "Kubernetes Pod Recovery",
                "Diagnose Kubernetes pod failures and propose safe recovery steps for CrashLoopBackOff, Pending, and OOMKilled states.",
                """
                ---
                name: kubernetes-pod-recovery
                description: Diagnose Kubernetes pod failures and propose safe recovery steps for CrashLoopBackOff, Pending, and OOMKilled states.
                ---

                # Kubernetes Pod Recovery

                ## When to Use

                Use this skill for Kubernetes pod startup failures, unhealthy workloads, scheduling failures, or repeated restarts.

                ## Required Inputs

                - Namespace, workload, pod name, and cluster
                - Pod status and recent events
                - Container logs and resource requests/limits
                - Recent image, config, secret, or deployment changes

                ## Workflow

                1. Identify pod phase and container termination reason.
                2. Check events for scheduling, image pull, probe, and volume failures.
                3. Compare resource limits with OOM or throttling evidence.
                4. Determine whether restart, rollback, or config correction is safest.
                5. Provide verification commands after recovery.

                ## Output Format

                ### Current State
                ### Evidence
                ### Likely Cause
                ### Recovery Plan
                ### Verification

                ## Rules

                - Prefer read-only kubectl checks before changes.
                - Warn before delete, rollout restart, scale, or rollback actions.
                - Include namespace in every command.
                """
            ),
            new SeedSkill(
                "database-performance-diagnosis",
                "Database Performance Diagnosis",
                "Investigate database latency, lock waits, slow queries, saturation, and connection pool pressure.",
                """
                ---
                name: database-performance-diagnosis
                description: Investigate database latency, lock waits, slow queries, saturation, and connection pool pressure.
                ---

                # Database Performance Diagnosis

                ## When to Use

                Use this skill when the user reports slow SQL, elevated database latency, lock waits, connection exhaustion, or timeout errors.

                ## Required Inputs

                - Database engine and version
                - Slow query samples or top SQL
                - Time range and affected application
                - CPU, memory, I/O, active sessions, and connection pool metrics

                ## Workflow

                1. Determine whether the symptom is query, lock, resource, or connection related.
                2. Compare current metrics with baseline.
                3. Identify top SQL, wait events, blocked sessions, or pool exhaustion.
                4. Recommend low-risk mitigations and longer-term fixes.
                5. Define post-change validation metrics.

                ## Output Format

                ### Symptom
                ### Evidence
                ### Bottleneck Type
                ### Immediate Mitigation
                ### Long-term Fix
                ### Validation

                ## Rules

                - Do not recommend destructive SQL without explicit approval.
                - Separate application pool saturation from database server saturation.
                - Prefer explain plans and wait events over guesses.
                """
            ),
            new SeedSkill(
                "deployment-rollback-planning",
                "Deployment Rollback Planning",
                "Assess failed or risky deployments and produce rollback, verification, and communication steps.",
                """
                ---
                name: deployment-rollback-planning
                description: Assess failed or risky deployments and produce rollback, verification, and communication steps.
                ---

                # Deployment Rollback Planning

                ## When to Use

                Use this skill when a deployment causes errors, metrics regression, failed health checks, or user-impacting behavior.

                ## Required Inputs

                - Service name, environment, and deployment version
                - Change summary and deployment time
                - Health check, logs, metrics, and alert evidence
                - Available rollback version or release artifact

                ## Workflow

                1. Confirm whether impact correlates with the deployment.
                2. Identify rollback prerequisites and data compatibility risks.
                3. Produce a rollback sequence with checkpoints.
                4. Define verification metrics and smoke tests.
                5. Draft concise stakeholder communication.

                ## Output Format

                ### Impact Assessment
                ### Rollback Readiness
                ### Rollback Steps
                ### Verification
                ### Communication Draft

                ## Rules

                - Check data migrations and irreversible changes before rollback.
                - Include clear go/no-go checkpoints.
                - Prefer staged rollback when blast radius is unclear.
                """
            )
        );
    }

    private record SeedSkill(String id, String name, String description, String markdown) {
    }
}
