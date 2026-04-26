# Supervisor Agent 50 轮测试报告

## 结论摘要

- Session：`20260426_3`
- 用户：`admin`
- Agent：`supervisor-agent`
- Provider：`custom_qwen3.5-35b-a3b`
- 模型：`qwen/qwen3.5-35b-a3b`
- 压测目标：每轮明确要求触发 Control Center 非破坏性工具调用
- 开始时间：`2026-04-26T02:00:57.778Z`
- 结束时间：`2026-04-26T02:27:55.994Z`
- 目标轮数：`50`
- 完成轮数：`32/50`
- 失败轮数：`1`
- 中止位置：第 `33` 轮
- 总耗时：`1,618,216 ms`，约 `26.97 min`
- 失败错误：`Error: round 33 timeout after 300000ms at Timeout._onTimeout ([stdin]:49:134) at listOnTimeout (node:internal/timers:605:17) at process.processTimers (node:internal/timers:541:7)`

本次测试未完成 50 轮，第 33 轮未成功收到正常 Finish。

## 工具调用统计

| 工具 | 调用次数 |
| --- | ---: |
| `control_center__list_services` | 15 |
| `control_center__read_service_logs` | 12 |
| `control_center__get_service_status` | 10 |
| `control_center__get_platform_status` | 9 |
| `control_center__get_realtime_metrics` | 8 |
| `control_center__get_agents_status` | 6 |
| `control_center__list_events` | 5 |
| `control_center__get_observability_data` | 4 |
| `control_center__read_service_config` | 4 |
| **合计** | **73** |

## Reasoning / Thinking 统计

- 出现 reasoning content 的轮次：`无`
- 出现 thinking content 的轮次：`1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32`

## Compaction 观察

- SSE `UpdateConversation` 轮次：`13, 24`
- 判断口径：只把 SSE 中真实出现的 `UpdateConversation` 计为明确 compaction/会话更新信号。

## Token 统计

- SSE 观测到的最大累计 token：`未观测到`
- 最后一轮观测到的累计 token：`未观测到`

## 耗时统计

- 总耗时：`1618216 ms`
- 平均每轮耗时：`49029 ms`
- P95 耗时：`67588 ms`
- 最大耗时：`992616 ms`
- 明显慢轮：
  - 第 13 轮：67588 ms，状态 finish
  - 第 24 轮：64350 ms，状态 finish
  - 第 33 轮：992616 ms，状态 timeout_or_abort，错误 Error: round 33 timeout after 300000ms at Timeout._onTimeout ([stdin]:49:134) at listOnTimeout (node:internal/timers:605:17) at process.processTimers (node:inte

## 每轮过程统计

| 轮次 | 状态 | 总耗时 ms | Submit ms | 首事件 ms | 首内容 ms | SSE 事件 | 工具次数 | 累计 token | 错误 |
| ---: | --- | ---: | ---: | ---: | ---: | --- | ---: | ---: | --- |
| 1 | finish | 8411 | 12 | 1065 | 1065 | Message:505, Finish:1 | 3 |  |  |
| 2 | finish | 11835 | 16 | 1872 | 1872 | Message:892, Finish:1 | 3 |  |  |
| 3 | finish | 19330 | 15 | 2001 | 2001 | Message:1559, Finish:1 | 2 |  |  |
| 4 | finish | 15051 | 18 | 1746 | 1746 | Message:1182, Finish:1 | 2 |  |  |
| 5 | finish | 18265 | 17 | 2178 | 2178 | Message:1242, Finish:1 | 3 |  |  |
| 6 | finish | 16498 | 20 | 2848 | 2848 | Message:1032, Finish:1 | 3 |  |  |
| 7 | finish | 15504 | 16 | 3778 | 3778 | Message:1066, Finish:1 | 2 |  |  |
| 8 | finish | 23071 | 17 | 1892 | 1892 | Message:1771, Finish:1 | 3 |  |  |
| 9 | finish | 19213 | 17 | 2516 | 2516 | Message:1363, Finish:1 | 2 |  |  |
| 10 | finish | 18460 | 14 | 2917 | 2917 | Message:1456, Finish:1 | 2 |  |  |
| 11 | finish | 20792 | 21 | 2879 | 2879 | Message:1667, Finish:1 | 2 |  |  |
| 12 | finish | 48697 | 18 | 51 | 51 | Message:3, Finish:1 | 0 |  |  |
| 13 | finish | 67588 | 17 | 50 | 50 | Message:516, UpdateConversation:1, Finish:1 | 3 |  |  |
| 14 | finish | 8347 | 21 | 1381 | 1381 | Message:364, Finish:1 | 3 |  |  |
| 15 | finish | 8291 | 20 | 2097 | 2097 | Message:761, Finish:1 | 1 |  |  |
| 16 | finish | 11417 | 23 | 1732 | 1732 | Message:770, Finish:1 | 3 |  |  |
| 17 | finish | 10943 | 23 | 2014 | 2014 | Message:721, Finish:1 | 3 |  |  |
| 18 | finish | 11909 | 24 | 1829 | 1829 | Message:1065, Finish:1 | 2 |  |  |
| 19 | finish | 9635 | 22 | 2071 | 2071 | Message:684, Finish:1 | 2 |  |  |
| 20 | finish | 15122 | 23 | 2701 | 2701 | Message:1030, Finish:1 | 3 |  |  |
| 21 | finish | 14324 | 22 | 2743 | 2743 | Message:667, Finish:1 | 3 |  |  |
| 22 | finish | 9924 | 35 | 2350 | 2350 | Message:529, Finish:1 | 2 |  |  |
| 23 | finish | 21500 | 22 | 2449 | 2449 | Message:1179, Finish:1 | 3 |  |  |
| 24 | finish | 64350 | 23 | 71 | 71 | Message:278, UpdateConversation:1, Finish:1 | 2 |  |  |
| 25 | finish | 40906 | 32 | 33511 | 33511 | Message:747, Finish:1 | 2 |  |  |
| 26 | finish | 9369 | 37 | 1609 | 1609 | Message:689, Finish:1 | 2 |  |  |
| 27 | finish | 7724 | 26 | 1560 | 1560 | Message:474, Finish:1 | 2 |  |  |
| 28 | finish | 10018 | 24 | 1709 | 1709 | Message:680, Finish:1 | 3 |  |  |
| 29 | finish | 32194 | 28 | 1903 | 1903 | Message:21, Finish:1 | 0 |  |  |
| 30 | finish | 8703 | 26 | 1538 | 1538 | Message:844, Finish:1 | 1 |  |  |
| 31 | finish | 14022 | 32 | 1547 | 1547 | Message:937, Finish:1 | 3 |  |  |
| 32 | finish | 13924 | 25 | 2199 | 2199 | Message:792, Finish:1 | 3 |  |  |
| 33 | timeout_or_abort | 992616 | 27 |  |  |  | 0 |  | Error: round 33 timeout after 300000ms at Timeout._onTimeout ([stdin]:49:134) at listOnTim |

## 第 33 轮失败分析

第 33 轮状态为 `timeout_or_abort`。

- Submit 耗时：`27 ms`
- 首个 SSE 事件耗时：`N/A ms`
- 首个内容耗时：`N/A ms`
- 已收到事件：`无`
- 已调用工具数：`0`
- 错误：`Error: round 33 timeout after 300000ms at Timeout._onTimeout ([stdin]:49:134) at listOnTimeout (node:internal/timers:605:17) at process.processTimers (node:internal/timers:541:7)`

若工具已返回但没有 Finish/Error，优先怀疑工具返回后的模型继续生成、provider 响应或 goosed 后续处理阶段。

## 观察到的风险点

1. 本次 prompt 明确指定工具，工具调用统计可用于衡量 supervisor-agent 在真实工具链路下的稳定性。
2. Control Center 工具本身通常很快；若出现慢轮，应重点区分首 token 前等待和工具返回后的模型续写耗时。
3. 可观测性未配置时，observability 工具会返回不可用信息，这是预期能力缺口，但仍会产生一次工具调用和后续模型总结。
4. 若出现无工具轮次，说明模型没有完全遵守 prompt 的工具调用约束，需在 agent system prompt 或测试 prompt 上进一步收紧。

## 建议

1. 后续对 supervisor-agent 做性能归因时，把 gateway submit、Control Center 工具耗时、首 token 等待、首 token 后生成分开记录。
2. 对服务日志类问题限制日志行数，避免日志内容过大导致模型后续总结变慢。
3. 如需避免无工具轮次，可在测试脚本中把“工具调用数为 0”视为失败并立即中止。

## 原始数据

- 结构化原始数据：`output/supervisor-50-round-20260426_3-tools.json`
- goosed 日志：`gateway/users/admin/agents/supervisor-agent/state/logs/server/2026-04-26/20260426_011301-goosed.log`
- control-center MCP 日志：`gateway/users/admin/agents/supervisor-agent/logs/mcp/control_center.log`
