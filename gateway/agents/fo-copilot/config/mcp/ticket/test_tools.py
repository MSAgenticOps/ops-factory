"""Logic tests for the ticket MCP adapter — no `mcp` dependency required.

Run:  python -m unittest test_tools   (from this directory)
"""

import asyncio
import json
import os
import tempfile
import unittest


def _fresh_store_module():
    """Import tools.py against a fresh temp store file and return the module."""
    tmp = tempfile.NamedTemporaryFile(suffix=".json", delete=False)
    tmp.close()
    os.unlink(tmp.name)  # store seeds itself when the file is absent
    os.environ["TICKET_MOCK_STORE"] = tmp.name
    import importlib

    import store as store_mod
    import tools as tools_mod
    importlib.reload(store_mod)
    importlib.reload(tools_mod)
    return tools_mod, tmp.name


def call(tools_mod, name, **args):
    return json.loads(asyncio.run(tools_mod.dispatch(name, args)))


class TicketToolsTest(unittest.TestCase):
    def setUp(self):
        self.tools, self.path = _fresh_store_module()

    def tearDown(self):
        if os.path.exists(self.path):
            os.unlink(self.path)

    def test_get_todo_returns_concise_summaries(self):
        res = call(self.tools, "get_todo")
        self.assertTrue(res["ok"])
        tickets = res["data"]["tickets"]
        self.assertGreaterEqual(len(tickets), 1)
        # concise view exposes high-signal fields and not the full timeline
        self.assertIn("slaResolveDueAt", tickets[0])
        self.assertNotIn("timeline", tickets[0])
        # only my tickets show up
        self.assertNotIn("INC-0998", {t["id"] for t in tickets})

    def test_state_context_concise_vs_detailed(self):
        concise = call(self.tools, "get_state_context", ticketId="INC-1042")
        self.assertNotIn("timeline", concise["data"])
        detailed = call(self.tools, "get_state_context", ticketId="INC-1042", format="detailed")
        self.assertIn("timeline", detailed["data"])
        self.assertIn("availableTransitions", detailed["data"])
        self.assertIn("recentComments", detailed["data"])

    def test_state_context_not_found_has_hint(self):
        res = call(self.tools, "get_state_context", ticketId="NOPE-1")
        self.assertFalse(res["ok"])
        self.assertEqual(res["error"]["code"], "TICKET_NOT_FOUND")
        self.assertTrue(res["error"]["hint"])

    def test_candidates_ranked_by_ticket(self):
        res = call(self.tools, "get_candidates", ticketId="INC-1042")
        self.assertTrue(res["ok"])
        top = res["data"]["candidates"][0]
        # INC-1042 is payments/checkout-api -> Li Wei should rank first
        self.assertEqual(top["id"], "li.wei")
        self.assertIn("matchedSkills", top)

    def test_create_then_appears_in_todo(self):
        created = call(self.tools, "create", title="New disk-space alert", type="Incident", priority="P3")
        self.assertTrue(created["ok"])
        new_id = created["data"]["id"]
        todo_ids = {t["id"] for t in call(self.tools, "get_todo")["data"]["tickets"]}
        self.assertIn(new_id, todo_ids)

    def test_update_fields(self):
        res = call(self.tools, "update", ticketId="REQ-2087", fields={"priority": "P2"})
        self.assertTrue(res["ok"])
        self.assertEqual(res["data"]["priority"], "P2")

    def test_comment_records_audit(self):
        res = call(self.tools, "comment", ticketId="INC-1042", body="Mitigation in progress.")
        self.assertTrue(res["ok"])
        detailed = call(self.tools, "get_state_context", ticketId="INC-1042", format="detailed")
        bodies = [c["summary"] for c in detailed["data"]["recentComments"]]
        self.assertIn("Mitigation in progress.", bodies)

    def test_transition_legal(self):
        # INC-1042 is in_progress -> resolve is legal
        res = call(self.tools, "transition", ticketId="INC-1042", transitionId="resolve", reason="rollback done")
        self.assertTrue(res["ok"], res)
        self.assertEqual(res["data"]["status"], "resolved")

    def test_transition_illegal_has_available_hint(self):
        # in_progress cannot jump straight to closed
        res = call(self.tools, "transition", ticketId="INC-1042", to="closed")
        self.assertFalse(res["ok"])
        self.assertEqual(res["error"]["code"], "ILLEGAL_TRANSITION")
        self.assertIn("Available from in_progress", res["error"]["hint"])

    def test_reassign_hands_off_ball(self):
        res = call(self.tools, "update_assignment", ticketId="INC-1042", owner="li.wei", reason="payments owner")
        self.assertTrue(res["ok"])
        todo_ids = {t["id"] for t in call(self.tools, "get_todo")["data"]["tickets"]}
        self.assertNotIn("INC-1042", todo_ids)

    def test_idempotency_key_accepted(self):
        res = call(self.tools, "comment", ticketId="INC-1042", body="note", operationId="op-1", followupId="f-1")
        self.assertTrue(res["ok"])

    def test_unknown_tool(self):
        res = json.loads(asyncio.run(self.tools.dispatch("nope", {})))
        self.assertFalse(res["ok"])
        self.assertEqual(res["error"]["code"], "UNKNOWN_TOOL")


if __name__ == "__main__":
    unittest.main()
