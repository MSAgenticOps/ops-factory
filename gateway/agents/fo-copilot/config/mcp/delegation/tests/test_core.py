#!/usr/bin/env python3
"""Unit tests for the delegation extension's pure helpers (stdlib `unittest`, no third-party deps needed)."""
import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

import core  # noqa: E402


class ResolveIdentityTest(unittest.TestCase):
    def test_resolves_user_and_agent(self):
        cwd = "/srv/ops-factory/gateway/users/alice/agents/agentA"
        self.assertEqual(core.resolve_identity(cwd), ("alice", "agentA"))

    def test_default_user(self):
        cwd = "/x/gateway/users/__default__/agents/supervisor-agent/sub"
        self.assertEqual(core.resolve_identity(cwd), ("__default__", "supervisor-agent"))

    def test_unresolved(self):
        self.assertEqual(core.resolve_identity("/tmp/somewhere/else"), (None, None))

    def test_ignores_macos_capital_users(self):
        cwd = "/Users/me/proj/gateway/users/bob/agents/qa-agent"
        self.assertEqual(core.resolve_identity(cwd), ("bob", "qa-agent"))

    def test_ignores_unrelated_users_segment_in_deploy_path(self):
        # An earlier 'users' dir in the absolute path must not be mistaken for the runtime owner.
        cwd = "/home/users/svc/gateway/users/alice/agents/fo-copilot"
        self.assertEqual(core.resolve_identity(cwd), ("alice", "fo-copilot"))

    def test_handles_userid_literally_agents(self):
        cwd = "/srv/gateway/users/agents/agents/fo-copilot"
        self.assertEqual(core.resolve_identity(cwd), ("agents", "fo-copilot"))


class ErrorDetailTest(unittest.TestCase):
    def test_extracts_spring_message(self):
        body = '{"status":409,"error":"Conflict","message":"nested agent-to-agent delegation is not allowed"}'
        self.assertEqual(core.error_detail(body), "nested agent-to-agent delegation is not allowed")

    def test_falls_back_to_error_field(self):
        self.assertEqual(core.error_detail('{"error":"Bad Request"}'), "Bad Request")

    def test_none_for_non_json(self):
        self.assertIsNone(core.error_detail("<html>502</html>"))


class ReadSessionIdTest(unittest.TestCase):
    def test_from_dict(self):
        self.assertEqual(core.read_session_id({"agent-session-id": "20260605_1"}), "20260605_1")

    def test_from_model_extra(self):
        class FakeMeta:
            model_extra = {"agent-session-id": "S9"}

        self.assertEqual(core.read_session_id(FakeMeta()), "S9")

    def test_none_and_empty(self):
        self.assertIsNone(core.read_session_id(None))
        self.assertIsNone(core.read_session_id({}))


class HeadersTest(unittest.TestCase):
    def test_full(self):
        headers = core.build_headers("sek", "alice", "agentA", "S1")
        self.assertEqual(headers["x-secret-key"], "sek")
        self.assertEqual(headers["x-user-id"], "alice")
        self.assertEqual(headers["x-a2a-origin"], "agentA")
        self.assertEqual(headers["x-a2a-origin-session"], "S1")

    def test_omits_blanks(self):
        headers = core.build_headers("", "alice", None, None)
        self.assertNotIn("x-a2a-origin", headers)
        self.assertNotIn("x-a2a-origin-session", headers)


class FrameTest(unittest.TestCase):
    def test_is_terminal(self):
        self.assertTrue(core.is_terminal({"type": "a2a_result"}))
        self.assertFalse(core.is_terminal({"type": "a2a_progress"}))
        self.assertFalse(core.is_terminal("not a dict"))

    def test_progress_payload(self):
        payload = core.progress_payload({
            "type": "a2a_progress", "target_agent": "agentB", "kind": "tool_call",
            "label": "read logs", "step": 3, "sub_session_id": "B1",
        })
        self.assertEqual(payload["type"], "a2a_progress")
        self.assertEqual(payload["label"], "read logs")
        self.assertEqual(payload["step"], 3)
        self.assertEqual(payload["target_agent"], "agentB")

    def test_result_completed(self):
        frame = {"type": "a2a_result", "status": "completed", "result": "Found 3 errors."}
        self.assertEqual(core.result_text(frame), "Found 3 errors.")

    def test_result_error(self):
        text = core.result_text({"type": "a2a_result", "status": "error", "error": "boom"})
        self.assertIn("boom", text)

    def test_result_timeout(self):
        text = core.result_text({"type": "a2a_result", "status": "timeout", "result": "partial"})
        self.assertIn("timed out", text)
        self.assertIn("partial", text)


if __name__ == "__main__":
    unittest.main()
