import http.server
import json
import os
import sys
import threading
import unittest
from contextlib import contextmanager

# Allow running from any directory
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from handlers import handle_fetch_url_content, handle_run_command


def _parse(raw: str) -> dict:
    return json.loads(raw)


@contextmanager
def _http_server(handler_fn):
    """Spin up a local HTTP server on an ephemeral port, yield its base URL."""

    def _make_handler(fn):
        class _Handler(http.server.BaseHTTPRequestHandler):
            def do_GET(self):
                fn(self)

            def log_message(self, *args):
                pass

        return _Handler

    srv = http.server.HTTPServer(("127.0.0.1", 0), _make_handler(handler_fn))
    port = srv.server_address[1]
    thread = threading.Thread(target=srv.serve_forever, daemon=True)
    thread.start()
    try:
        yield f"http://127.0.0.1:{port}"
    finally:
        srv.shutdown()
        thread.join(timeout=2)


class TestRunCommand(unittest.IsolatedAsyncioTestCase):

    async def test_accepts_simple_whitelisted_command(self):
        result = _parse(await handle_run_command({"command": "pwd", "cwd": os.getcwd()}))
        self.assertTrue(result["ok"])
        self.assertEqual(result["tool"], "run_command")
        self.assertEqual(result["data"]["command"], "pwd")
        self.assertEqual(result["data"]["exit_code"], 0)

    async def test_splits_inline_args_from_command_string(self):
        result = _parse(await handle_run_command({"command": "ls -d .", "cwd": os.getcwd()}))
        self.assertTrue(result["ok"])
        self.assertEqual(result["data"]["command"], "ls")
        self.assertEqual(result["data"]["args"], ["-d", "."])
        self.assertEqual(result["data"]["exit_code"], 0)

    async def test_rejects_non_whitelisted_commands(self):
        result = _parse(await handle_run_command({"command": "rm -rf tmp", "cwd": os.getcwd()}))
        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["code"], "COMMAND_NOT_ALLOWED")

    async def test_rejects_mutating_commands_by_default(self):
        for cmd in ["sed -i s/a/b/g file.txt", "find . -delete"]:
            with self.subTest(cmd=cmd):
                result = _parse(await handle_run_command({"command": cmd, "cwd": os.getcwd()}))
                self.assertFalse(result["ok"])
                self.assertEqual(result["error"]["code"], "COMMAND_NOT_ALLOWED")

    async def test_returns_structured_error_for_missing_command(self):
        result = _parse(await handle_run_command({}))
        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["code"], "COMMAND_REQUIRED")

    async def test_rejects_shell_operators_in_arguments(self):
        result = _parse(await handle_run_command({
            "command": "ls",
            "args": [".", "&&", "pwd"],
            "cwd": os.getcwd(),
        }))
        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["code"], "UNSAFE_ARGUMENT")

    async def test_rejects_cwd_outside_allowed_roots(self):
        prev = os.environ.get("LOCAL_TINY_COMMAND_ROOTS")
        os.environ["LOCAL_TINY_COMMAND_ROOTS"] = os.getcwd()
        try:
            result = _parse(await handle_run_command({"command": "pwd", "cwd": "/"}))
            self.assertFalse(result["ok"])
            self.assertEqual(result["error"]["code"], "CWD_NOT_ALLOWED")
        finally:
            if prev is None:
                os.environ.pop("LOCAL_TINY_COMMAND_ROOTS", None)
            else:
                os.environ["LOCAL_TINY_COMMAND_ROOTS"] = prev


class TestFetchUrlContent(unittest.IsolatedAsyncioTestCase):

    async def test_fetches_localhost_text(self):
        def handler(req):
            body = b"hello local tiny agent"
            req.send_response(200)
            req.send_header("Content-Type", "text/plain")
            req.send_header("Content-Length", str(len(body)))
            req.end_headers()
            req.wfile.write(body)

        with _http_server(handler) as url:
            result = _parse(await handle_fetch_url_content({"url": url}))

        self.assertTrue(result["ok"])
        self.assertEqual(result["data"]["status"], 200)
        self.assertEqual(result["data"]["body"], "hello local tiny agent")

    async def test_accepts_url_without_scheme(self):
        def handler(req):
            body = json.dumps({"ok": True}).encode()
            req.send_response(200)
            req.send_header("Content-Type", "application/json")
            req.send_header("Content-Length", str(len(body)))
            req.end_headers()
            req.wfile.write(body)

        with _http_server(handler) as url:
            no_scheme = url.replace("http://", "", 1)
            result = _parse(await handle_fetch_url_content({"url": no_scheme}))

        self.assertTrue(result["ok"])
        self.assertEqual(result["data"]["status"], 200)
        self.assertTrue(json.loads(result["data"]["body"])["ok"])

    async def test_truncates_long_content(self):
        def handler(req):
            body = b"x" * 4096
            req.send_response(200)
            req.send_header("Content-Type", "text/plain")
            req.send_header("Content-Length", str(len(body)))
            req.end_headers()
            req.wfile.write(body)

        with _http_server(handler) as url:
            result = _parse(await handle_fetch_url_content({"url": url, "max_bytes": 1024}))

        self.assertTrue(result["ok"])
        self.assertTrue(result["truncated"])
        self.assertEqual(len(result["data"]["body"]), 1024)

    async def test_returns_structured_error_for_missing_url(self):
        result = _parse(await handle_fetch_url_content({}))
        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["code"], "URL_REQUIRED")

    async def test_rejects_non_local_hosts_by_default(self):
        result = _parse(await handle_fetch_url_content({"url": "https://example.com"}))
        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["code"], "HOST_NOT_ALLOWED")

    async def test_rejects_redirect_to_disallowed_host(self):
        def handler(req):
            req.send_response(302)
            req.send_header("Location", "https://example.com/")
            req.end_headers()

        with _http_server(handler) as url:
            result = _parse(await handle_fetch_url_content({"url": url}))

        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["code"], "REDIRECT_HOST_NOT_ALLOWED")


if __name__ == "__main__":
    unittest.main()
