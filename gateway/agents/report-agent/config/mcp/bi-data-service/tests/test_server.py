#!/usr/bin/env python3
import json
import sys
import os
import unittest

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from server import (
    RuntimeConfig, ToolExecutionError, _apply_filter, _apply_filters,
    _select_fields, _sort_rows, _compute_single_metric, _parse_date_safe,
    _bucket_key, _resolve_time_range, handle_request, format_tool_result,
    VALID_TAB_IDS, VALID_DOMAINS, VALID_METRICS, VALID_TREND_METRICS,
    VALID_INTERVALS, VALID_OPERATORS,
)


class TestFilter(unittest.TestCase):
    def test_equals(self):
        self.assertTrue(_apply_filter({"Status": "Completed"}, {"field": "Status", "operator": "equals", "value": "Completed"}))
        self.assertFalse(_apply_filter({"Status": "Open"}, {"field": "Status", "operator": "equals", "value": "Completed"}))
        self.assertTrue(_apply_filter({"Status": "completed"}, {"field": "Status", "operator": "equals", "value": "Completed"}))

    def test_not_equals(self):
        self.assertTrue(_apply_filter({"Status": "Open"}, {"field": "Status", "operator": "not_equals", "value": "Completed"}))
        self.assertFalse(_apply_filter({"Status": "Completed"}, {"field": "Status", "operator": "not_equals", "value": "Completed"}))

    def test_contains(self):
        self.assertTrue(_apply_filter({"Name": "Network Error"}, {"field": "Name", "operator": "contains", "value": "error"}))
        self.assertFalse(_apply_filter({"Name": "Network"}, {"field": "Name", "operator": "contains", "value": "error"}))

    def test_starts_with(self):
        self.assertTrue(_apply_filter({"ID": "INC001"}, {"field": "ID", "operator": "starts_with", "value": "INC"}))
        self.assertFalse(_apply_filter({"ID": "CHG001"}, {"field": "ID", "operator": "starts_with", "value": "INC"}))

    def test_greater_than(self):
        self.assertTrue(_apply_filter({"Time": "120"}, {"field": "Time", "operator": "greater_than", "value": "60"}))
        self.assertFalse(_apply_filter({"Time": "30"}, {"field": "Time", "operator": "greater_than", "value": "60"}))

    def test_less_than(self):
        self.assertTrue(_apply_filter({"Time": "30"}, {"field": "Time", "operator": "less_than", "value": "60"}))
        self.assertFalse(_apply_filter({"Time": "120"}, {"field": "Time", "operator": "less_than", "value": "60"}))

    def test_in(self):
        f = {"field": "Priority", "operator": "in", "value": ["P1", "P2"]}
        self.assertTrue(_apply_filter({"Priority": "P1"}, f))
        self.assertTrue(_apply_filter({"Priority": "P2"}, f))
        self.assertFalse(_apply_filter({"Priority": "P3"}, f))

    def test_missing_field(self):
        self.assertFalse(_apply_filter({}, {"field": "Status", "operator": "equals", "value": "Completed"}))

    def test_apply_filters_and(self):
        rows = [
            {"Status": "Completed", "Priority": "P1"},
            {"Status": "Completed", "Priority": "P2"},
            {"Status": "Open", "Priority": "P1"},
        ]
        filters = [
            {"field": "Status", "operator": "equals", "value": "Completed"},
            {"field": "Priority", "operator": "equals", "value": "P1"},
        ]
        result = _apply_filters(rows, filters)
        self.assertEqual(len(result), 1)
        self.assertEqual(result[0]["Status"], "Completed")


class TestSelectAndSort(unittest.TestCase):
    def test_select_fields(self):
        rows = [{"A": "1", "B": "2", "C": "3"}]
        result = _select_fields(rows, ["A", "C"])
        self.assertEqual(result, [{"A": "1", "C": "3"}])

    def test_select_all(self):
        rows = [{"A": "1", "B": "2"}]
        result = _select_fields(rows, None)
        self.assertEqual(result, rows)

    def test_sort_desc(self):
        rows = [{"V": "3"}, {"V": "1"}, {"V": "2"}]
        result = _sort_rows(rows, "V", "desc")
        self.assertEqual([r["V"] for r in result], ["3", "2", "1"])

    def test_sort_asc(self):
        rows = [{"V": "3"}, {"V": "1"}, {"V": "2"}]
        result = _sort_rows(rows, "V", "asc")
        self.assertEqual([r["V"] for r in result], ["1", "2", "3"])


class TestComputeMetric(unittest.TestCase):
    def test_count(self):
        rows = [{"A": "1"}, {"A": "2"}, {"A": "3"}]
        self.assertEqual(_compute_single_metric("count", rows, {}), 3)

    def test_avg(self):
        rows = [{"V": "10"}, {"V": "20"}, {"V": "30"}, {"V": "0"}]
        self.assertEqual(_compute_single_metric("avg", rows, {"field": "V"}), 20.0)

    def test_sum(self):
        rows = [{"V": "10"}, {"V": "20"}, {"V": "30"}]
        self.assertEqual(_compute_single_metric("sum", rows, {"field": "V"}), 60.0)

    def test_percentage(self):
        rows = [{"SLA": "Yes"}, {"SLA": "Yes"}, {"SLA": "No"}]
        result = _compute_single_metric("percentage", rows, {"field": "SLA", "value": "Yes"})
        self.assertAlmostEqual(result, 66.7, places=0)

    def test_distribution(self):
        rows = [{"Cat": "A"}, {"Cat": "A"}, {"Cat": "B"}]
        result = _compute_single_metric("distribution", rows, {"field": "Cat"})
        self.assertEqual(len(result), 2)
        self.assertEqual(result[0]["value"], "A")
        self.assertEqual(result[0]["count"], 2)


class TestParseDate(unittest.TestCase):
    def test_iso_format(self):
        d = _parse_date_safe("2024-01-15T10:30:00")
        self.assertIsNotNone(d)
        self.assertEqual(d.year, 2024)
        self.assertEqual(d.month, 1)

    def test_date_only(self):
        d = _parse_date_safe("2024-01-15")
        self.assertIsNotNone(d)

    def test_empty(self):
        self.assertIsNone(_parse_date_safe(""))
        self.assertIsNone(_parse_date_safe("   "))


class TestBucketKey(unittest.TestCase):
    def test_hour(self):
        from datetime import datetime
        d = datetime(2024, 3, 15, 14, 30)
        self.assertEqual(_bucket_key(d, "hour"), "2024-03-15T14:00")

    def test_day(self):
        from datetime import datetime
        d = datetime(2024, 3, 15, 14, 30)
        self.assertEqual(_bucket_key(d, "day"), "2024-03-15")

    def test_month(self):
        from datetime import datetime
        d = datetime(2024, 3, 15)
        self.assertEqual(_bucket_key(d, "month"), "2024-03")


class TestResolveTimeRange(unittest.TestCase):
    def test_last_7d(self):
        s, e = _resolve_time_range("last_7d")
        self.assertIsNotNone(s)
        self.assertIsNotNone(e)

    def test_none(self):
        s, e = _resolve_time_range(None)
        self.assertIsNone(s)
        self.assertIsNone(e)

    def test_dict(self):
        s, e = _resolve_time_range({"start": "2024-01-01", "end": "2024-01-31"})
        self.assertIsNotNone(s)
        self.assertIsNotNone(e)


class TestJsonRpc(unittest.TestCase):
    def test_initialize(self):
        config = RuntimeConfig("http://localhost:8093", "/data", 30)
        msg = {"jsonrpc": "2.0", "id": 1, "method": "initialize", "params": {}}
        resp = handle_request(msg, config)
        self.assertEqual(resp["id"], 1)
        self.assertIn("result", resp)
        self.assertEqual(resp["result"]["serverInfo"]["name"], "bi-data-service")

    def test_ping(self):
        config = RuntimeConfig("http://localhost:8093", "/data", 30)
        msg = {"jsonrpc": "2.0", "id": 2, "method": "ping"}
        resp = handle_request(msg, config)
        self.assertIn("result", resp)

    def test_tools_list(self):
        config = RuntimeConfig("http://localhost:8093", "/data", 30)
        msg = {"jsonrpc": "2.0", "id": 3, "method": "tools/list"}
        resp = handle_request(msg, config)
        tools = resp["result"]["tools"]
        tool_names = [t["name"] for t in tools]
        self.assertIn("get_bi_metrics", tool_names)
        self.assertEqual(len(tools), 8)

    def test_unknown_method(self):
        config = RuntimeConfig("http://localhost:8093", "/data", 30)
        msg = {"jsonrpc": "2.0", "id": 4, "method": "unknown"}
        resp = handle_request(msg, config)
        self.assertIn("error", resp)

    def test_unknown_tool(self):
        config = RuntimeConfig("http://localhost:8093", "/data", 30)
        msg = {"jsonrpc": "2.0", "id": 5, "method": "tools/call", "params": {"name": "nonexistent"}}
        resp = handle_request(msg, config)
        self.assertIn("error", resp)

    def test_invalid_metrics_domain(self):
        config = RuntimeConfig("http://localhost:8093", "/data", 30)
        msg = {"jsonrpc": "2.0", "id": 6, "method": "tools/call", "params": {"name": "get_bi_metrics", "arguments": {"domain": "invalid"}}}
        resp = handle_request(msg, config)
        result = resp["result"]
        self.assertTrue(result.get("isError"))


class TestFormatToolResult(unittest.TestCase):
    def test_string(self):
        r = format_tool_result("hello")
        self.assertEqual(r["content"][0]["text"], "hello")
        self.assertFalse(r["isError"])

    def test_dict(self):
        r = format_tool_result({"key": "value"})
        parsed = json.loads(r["content"][0]["text"])
        self.assertEqual(parsed["key"], "value")

    def test_error(self):
        r = format_tool_result("error msg", is_error=True)
        self.assertTrue(r["isError"])

    def test_truncation(self):
        from server import MAX_RESULT_CHARS
        big_data = {"data": "x" * (MAX_RESULT_CHARS + 1000)}
        r = format_tool_result(big_data)
        text = r["content"][0]["text"]
        self.assertLess(len(text), MAX_RESULT_CHARS + 200)
        self.assertIn("结果已截断", text)


class TestMetricsHandler(unittest.TestCase):
    def test_get_bi_metrics_calls_correct_endpoint(self):
        from server import _handle_get_bi_metrics
        import unittest.mock as mock
        with mock.patch("server._bi_request", return_value={"overallScore": 85.0}) as mock_req:
            result = _handle_get_bi_metrics({"domain": "executive"}, RuntimeConfig(bi_service_url="http://localhost", bi_data_dir="/tmp", timeout_seconds=30))
        self.assertEqual(result["overallScore"], 85.0)
        mock_req.assert_called_once_with("GET", "/metrics/executive", mock.ANY, None)

    def test_invalid_domain_raises(self):
        from server import _handle_get_bi_metrics
        with self.assertRaises(Exception):
            _handle_get_bi_metrics({"domain": "bogus"}, RuntimeConfig(bi_service_url="http://localhost", bi_data_dir="/tmp", timeout_seconds=30))


if __name__ == "__main__":
    unittest.main()
