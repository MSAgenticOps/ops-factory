package com.huawei.opsfactory.gateway.qos.scheduler;

import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.dv.DvClient;
import com.huawei.opsfactory.gateway.qos.dv.DvEnvironmentInfo;
import com.huawei.opsfactory.gateway.qos.model.AlarmDetailData;
import com.huawei.opsfactory.gateway.qos.model.AlarmInfo;
import com.huawei.opsfactory.gateway.qos.model.DnCluster;
import com.huawei.opsfactory.gateway.qos.model.DnElement;
import com.huawei.opsfactory.gateway.qos.model.DnRegistry;
import com.huawei.opsfactory.gateway.qos.model.IndicatorDetailData;
import com.huawei.opsfactory.gateway.qos.model.IndicatorNormalizeData;
import com.huawei.opsfactory.gateway.qos.model.IndicatorRawData;
import com.huawei.opsfactory.gateway.qos.model.PerformanceDataResult;
import com.huawei.opsfactory.gateway.qos.model.PerformanceIndicatorScope;
import com.huawei.opsfactory.gateway.qos.model.ProductConfigRule;
import com.huawei.opsfactory.gateway.qos.store.AlarmDetailDataStore;
import com.huawei.opsfactory.gateway.qos.store.DnRegistryStore;
import com.huawei.opsfactory.gateway.qos.store.IndicatorDetailDataStore;
import com.huawei.opsfactory.gateway.qos.store.IndicatorNormalizeDataStore;
import com.huawei.opsfactory.gateway.qos.store.IndicatorRawDataStore;
import com.huawei.opsfactory.gateway.qos.store.PerformanceIndicatorScopeStore;
import com.huawei.opsfactory.gateway.qos.store.ProductConfigRuleStore;
import com.huawei.opsfactory.gateway.service.QosCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class QosCollectionScheduler {

    private static final Logger log = LoggerFactory.getLogger(QosCollectionScheduler.class);

    private final GatewayProperties properties;
    private final DvClient dvClient;
    private final QosCalculationService calculationService;
    private final PerformanceIndicatorScopeStore scopeStore;
    private final ProductConfigRuleStore configStore;
    private final IndicatorRawDataStore rawDataStore;
    private final IndicatorDetailDataStore detailDataStore;
    private final IndicatorNormalizeDataStore normalizeDataStore;
    private final AlarmDetailDataStore alarmDetailDataStore;
    private final DnRegistryStore dnRegistryStore;

    public QosCollectionScheduler(GatewayProperties properties,
            DvClient dvClient,
            QosCalculationService calculationService,
            PerformanceIndicatorScopeStore scopeStore,
            ProductConfigRuleStore configStore,
            IndicatorRawDataStore rawDataStore,
            IndicatorDetailDataStore detailDataStore,
            IndicatorNormalizeDataStore normalizeDataStore,
            AlarmDetailDataStore alarmDetailDataStore,
            DnRegistryStore dnRegistryStore) {
        this.properties = properties;
        this.dvClient = dvClient;
        this.calculationService = calculationService;
        this.scopeStore = scopeStore;
        this.configStore = configStore;
        this.rawDataStore = rawDataStore;
        this.detailDataStore = detailDataStore;
        this.normalizeDataStore = normalizeDataStore;
        this.alarmDetailDataStore = alarmDetailDataStore;
        this.dnRegistryStore = dnRegistryStore;
    }

    @Scheduled(fixedDelayString = "${gateway.qos.collection-interval-ms:300000}")
    public void collectAvailabilityAndPerformance() {
        if (!properties.getQos().isEnabled()) return;
        log.info("QoS collection: starting availability and performance collection");

        List<GatewayProperties.Qos.DvEnvironment> dvEnvs = properties.getQos().getDvEnvironments();
        if (dvEnvs.isEmpty()) { log.warn("QoS collection: no DV environments configured"); return; }

        List<PerformanceIndicatorScope> scopes = scopeStore.loadAll();
        if (scopes.isEmpty()) { log.warn("QoS collection: no performance indicator scopes configured"); return; }

        long endTime = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        long startTime = endTime - properties.getQos().getCollectionIntervalMs();

        for (GatewayProperties.Qos.DvEnvironment dvEnvConfig : dvEnvs) {
            DvEnvironmentInfo envInfo = toDvEnvironmentInfo(dvEnvConfig);
            String envCode = envInfo.getEnvCode();

            List<PerformanceIndicatorScope> envScopes = scopes.stream()
                    .filter(s -> s.getAgentSolutionType().equals(envInfo.getAgentSolutionType()))
                    .collect(Collectors.toList());
            if (envScopes.isEmpty()) continue;

            List<DnCluster> clusters = loadClusters(envCode);
            // type -> dn -> indicator scores
            Map<String, Map<String, List<BigDecimal>>> neScoreSums = new LinkedHashMap<>();

            for (DnCluster cluster : clusters) {
                for (DnElement element : (cluster.getElements() != null ? cluster.getElements() : List.<DnElement>of())) {
                    List<String> dns = buildDns(envInfo, element);
                    for (PerformanceIndicatorScope scope : envScopes) {
                        List<PerformanceDataResult> perfData = dvClient.fetchPerformanceData(
                            envInfo, scope.getMoType(), scope.getMeasUnitKey(), dns, startTime, endTime);
                        if (perfData == null || perfData.isEmpty()) continue;

                        for (PerformanceDataResult pr : perfData) {
                            appendRawData(envCode, pr, endTime);
                            BigDecimal score = collectScore(neScoreSums, scope, pr);
                            appendDetailData(envCode, scope, pr, endTime, score);
                        }
                    }
                }
            }

            // aggregate: per-type average across all network elements
            for (Map.Entry<String, Map<String, List<BigDecimal>>> typeEntry : neScoreSums.entrySet()) {
                BigDecimal totalAvg = BigDecimal.ZERO;
                int neCount = 0;
                for (List<BigDecimal> scores : typeEntry.getValue().values()) {
                    if (!scores.isEmpty()) {
                        totalAvg = totalAvg.add(avg(scores));
                        neCount++;
                    }
                }
                if (neCount > 0) {
                    appendNormalize(envCode, typeEntry.getKey(), totalAvg.divide(BigDecimal.valueOf(neCount), 2, RoundingMode.HALF_UP), endTime);
                }
            }
        }
        log.info("QoS collection: availability and performance collection completed");
    }

    @Scheduled(fixedDelayString = "${gateway.qos.collection-interval-ms:300000}")
    public void collectResourceData() {
        if (!properties.getQos().isEnabled()) return;
        log.info("QoS collection: starting resource (alarm) collection");

        List<GatewayProperties.Qos.DvEnvironment> dvEnvs = properties.getQos().getDvEnvironments();
        List<ProductConfigRule> configs = configStore.loadAll();
        if (dvEnvs.isEmpty() || configs.isEmpty()) { log.warn("QoS collection: no DV environments or product configs configured"); return; }

        long endTime = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        long startTime = endTime - properties.getQos().getCollectionIntervalMs();

        for (GatewayProperties.Qos.DvEnvironment dvEnvConfig : dvEnvs) {
            DvEnvironmentInfo envInfo = toDvEnvironmentInfo(dvEnvConfig);
            String envCode = envInfo.getEnvCode();

            ProductConfigRule config = configs.stream()
                    .filter(c -> c.getAgentSolutionType().equals(envInfo.getAgentSolutionType()))
                    .findFirst().orElse(null);
            int iMax = config != null && config.getAlarmScoreMax() != null ? config.getAlarmScoreMax() : 20;
            Map<String, BigDecimal> alarmWeights = resolveAlarmWeights(config);

            List<DnCluster> clusters = loadClusters(envCode);
            List<AlarmInfo> allAlarms = new ArrayList<>();
            for (DnCluster cluster : clusters) {
                for (DnElement element : (cluster.getElements() != null ? cluster.getElements() : List.<DnElement>of())) {
                    List<String> alarmDns = buildDns(envInfo, element);
                    List<AlarmInfo> alarms = dvClient.fetchCurrentAlarms(envInfo, startTime, endTime, null, alarmDns);
                    if (alarms != null) allAlarms.addAll(alarms);
                }
            }

            for (AlarmInfo alarm : allAlarms) {
                AlarmDetailData detail = new AlarmDetailData();
                detail.setCode(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
                detail.setEnvCode(envCode);
                detail.setAlarmId(alarm.getAlarmId());
                detail.setAlarmName(alarm.getAlarmName());
                detail.setSeverity(alarm.getSeverity());
                detail.setDn(alarm.getDn());
                detail.setMeName(alarm.getMeName());
                detail.setOccurUtc(alarm.getOccurUtc());
                detail.setCount(alarm.getCount());
                detail.setMoi(alarm.getMoi());
                detail.setAdditionalInformation(alarm.getAdditionalInformation());
                detail.setTimestamp(endTime);
                alarmDetailDataStore.append(detail);
            }

            if (!allAlarms.isEmpty()) {
                BigDecimal rScore = calculationService.calculateResourceScore(allAlarms, alarmWeights, iMax);
                appendNormalize(envCode, "R", rScore, endTime);
            }
        }
        log.info("QoS collection: resource collection completed");
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupOldData() {
        if (!properties.getQos().isEnabled()) return;
        log.info("QoS cleanup: removing expired data files");
        rawDataStore.cleanup();
        detailDataStore.cleanup();
        normalizeDataStore.cleanup();
    }

    // ---- helpers ----

    private List<DnCluster> loadClusters(String envCode) {
        DnRegistry dnReg = dnRegistryStore.loadAll().stream()
                .filter(r -> envCode.equals(r.getEnvCode())).findFirst().orElse(null);
        return (dnReg != null && dnReg.getClusters() != null) ? dnReg.getClusters() : List.of();
    }

    private List<String> buildDns(DvEnvironmentInfo envInfo, DnElement element) {
        List<String> mosDns = dvClient.fetchMos(envInfo, List.of(element.getDn()));
        List<String> dns = new ArrayList<>();
        dns.add(element.getDn());
        if (mosDns != null) dns.addAll(mosDns);
        return dns;
    }

    private void appendRawData(String envCode, PerformanceDataResult pr, long ts) {
        IndicatorRawData raw = new IndicatorRawData();
        raw.setCode(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        raw.setEnvCode(envCode);
        raw.setDn(pr.getDn());
        raw.setMoType(pr.getMoType());
        raw.setNeName(pr.getNeName());
        raw.setValues(pr.getValues());
        raw.setTimestamp(ts);
        rawDataStore.append(raw);
    }

    private void appendDetailData(String envCode, PerformanceIndicatorScope scope, PerformanceDataResult pr,
        long ts, BigDecimal score) {
        Map<String, String> filtered = new LinkedHashMap<>();
        if (scope.getMeasTypeKeys() != null && pr.getValues() != null) {
            for (String key : scope.getMeasTypeKeys().split(",")) {
                String k = key.trim();
                if (pr.getValues().containsKey(k)) filtered.put(k, pr.getValues().get(k));
            }
        }
        IndicatorDetailData detail = new IndicatorDetailData();
        detail.setCode(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        detail.setEnvCode(envCode);
        detail.setIndicatorCode(scope.getIndicatorCode());
        detail.setIndicatorName(scope.getIndicatorName());
        detail.setType(scope.getType());
        detail.setDn(pr.getDn());
        detail.setDnIndicatorValue(score == null ? BigDecimal.ZERO : score.setScale(2, RoundingMode.HALF_UP));
        detail.setValues(filtered);
        detail.setTimestamp(ts);
        detailDataStore.append(detail);
    }

    private BigDecimal collectScore(Map<String, Map<String, List<BigDecimal>>> neScoreSums,
            PerformanceIndicatorScope scope, PerformanceDataResult pr) {
        if (scope.getThresholds() == null || scope.getThresholds().isBlank()) return null;
        if (scope.getMeasTypeKeys() == null || scope.getMeasTypeKeys().isBlank()) return null;
        String firstKey = scope.getMeasTypeKeys().split(",")[0].trim();
        String rawValue = pr.getValues() != null ? pr.getValues().get(firstKey) : null;
        if (rawValue == null) return null;
        try {
            BigDecimal value = new BigDecimal(rawValue);
            BigDecimal score = interpolateThreshold(scope.getThresholds(), value);
            neScoreSums.computeIfAbsent(scope.getType(), k -> new LinkedHashMap<>())
                .computeIfAbsent(pr.getDn(), k -> new ArrayList<>())
                .add(score);
            return score;
        } catch (NumberFormatException e) {
            log.warn("Failed to parse value for key {}: {}", firstKey, rawValue);
            return null;
        }
    }

    private void appendNormalize(String envCode, String type, BigDecimal value, long timestamp) {
        IndicatorNormalizeData data = new IndicatorNormalizeData();
        data.setCode(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        data.setEnvCode(envCode);
        data.setType(type);
        data.setIndicatorValue(value);
        data.setTimestamp(timestamp);
        normalizeDataStore.append(data);
    }

    private DvEnvironmentInfo toDvEnvironmentInfo(GatewayProperties.Qos.DvEnvironment config) {
        return new DvEnvironmentInfo(config.getEnvCode(), config.getAgentSolutionType(),
                config.getServerUrl(), config.getUtmUser(), config.getUtmPassword(),
                config.getCrtContent(), config.getCrtFileName(), config.getDns());
    }

    private Map<String, BigDecimal> resolveAlarmWeights(ProductConfigRule config) {
        Map<String, BigDecimal> weights = new LinkedHashMap<>();
        if (config != null && config.getAlarmWeight() != null && !config.getAlarmWeight().isBlank()) {
            String[] parts = config.getAlarmWeight().split(",");
            if (parts.length >= 3) {
                try {
                    weights.put("1", new BigDecimal(parts[0].trim()));
                    weights.put("2", new BigDecimal(parts[1].trim()));
                    weights.put("3", new BigDecimal(parts[2].trim()));
                    return weights;
                } catch (NumberFormatException e) {
                    log.warn("Invalid alarm weight config: {}, using defaults", config.getAlarmWeight());
                }
            }
        }
        weights.put("1", new BigDecimal("1.0"));
        weights.put("2", new BigDecimal("0.6"));
        weights.put("3", new BigDecimal("0.3"));
        return weights;
    }

    /** Parse threshold string like "0.3:0;0.6:0.3;0.9:0.6;0.95:0.95" and interpolate score for given value. */
    static BigDecimal interpolateThreshold(String thresholds, BigDecimal value) {
        String[] pairs = thresholds.split(";");
        BigDecimal prevScore = BigDecimal.ZERO, prevValue = BigDecimal.ZERO;
        for (String pair : pairs) {
            String[] sv = pair.split(":");
            if (sv.length != 2) continue;
            BigDecimal threshold = new BigDecimal(sv[0].trim());
            BigDecimal score = new BigDecimal(sv[1].trim());
            int cmp = value.compareTo(threshold);
            if (cmp <= 0) {
                if (threshold.compareTo(prevValue) == 0) return score;
                BigDecimal ratio = value.subtract(prevValue).divide(threshold.subtract(prevValue), 8, RoundingMode.HALF_UP);
                return prevScore.add(score.subtract(prevScore).multiply(ratio));
            }
            prevScore = score;
            prevValue = threshold;
        }
        return prevScore;
    }

    private static BigDecimal avg(List<BigDecimal> list) {
        return list.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(list.size()), 4, RoundingMode.HALF_UP);
    }
}