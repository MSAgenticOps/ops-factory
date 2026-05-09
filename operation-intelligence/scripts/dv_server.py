"""Mock server on port 26335 for testing gateway session and related APIs."""

import json
import os
import random
import ssl
import subprocess
import time
from http.server import HTTPServer, BaseHTTPRequestHandler

PORT = 26335


class MockHandler(BaseHTTPRequestHandler):

    def _send_json(self, status: int, body):
        payload = json.dumps(body, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(payload)))
        self.end_headers()
        self.wfile.write(payload)

    def _read_json(self):
        length = int(self.headers.get("Content-Length", 0))
        raw = self.rfile.read(length) if length else b""
        return json.loads(raw) if raw else {}

    # ---- /rest/plat/smapp/v1/sessions ----
    def do_PUT_sessions(self):
        self._send_json(200, {
            "accessSession": "x-vw2kbwdgqk3ulhs8g886aocamqpidgqlekka3tdfunpfdihgtfg6dc3zfvhfrwmnk73t1i5h9cfxs5obnw4abyns4b6o5jjzvvbuk7o508ukhdhcuqqpem6plc8arso9",
            "roaRand": "3aaf172fdb8eaa9e0e2cef820ec8a8817364cafd46df482a",
            "expires": 1800,
            "additionalInfo": None,
        })

    # ---- /rest/eammimservice/v1/openapi/mit/mos ----
    def do_POST_mos(self):
        self._send_json(200, [
            {
                "@moClass": "com.huawei.oms.eam.mo.Application",
                "sequenceNo": 161899,
                "dn": "540d4035-e461-4c95-acb3-0f1f187c374d_722_NSLBSHOP",
                "fdn": "1.161881.161899",
                "typeID": 0,
                "type": "com.huawei.itpaas.platformservice.nslb",
                "version": "1.0",
                "displayName": "bes_pr1_NSLBSHOP",
                "name": "bes_pr1_NSLBSHOP",
                "vendor": "Huawei",
                "description": None,
                "owner": None,
                "createdTime": 1739237893186,
                "medNodeID": "medNode_Master",
                "alarmStatus": "Cleared",
                "clientProperties": {
                    "softDelete": True,
                    "TypeCode": "",
                    "appUniqueKey": "540d4035-e461-4c95-acb3-0f1f187c374d_722_NSLBSHOP",
                    "kind": "Application",
                    "isk8sApplication": True,
                    "namespace": "bes",
                    "hwsRuntimeType": "container",
                    "solutionId": "540d4035-e461-4c95-acb3-0f1f187c374d",
                    "K8SType": True,
                    "Code": "",
                    "provinceId": "NA",
                    "hwsApplicationName": "NSLBSHOP",
                },
                "children": [
                    "ab9902c3-e6c8-4d87-96b6-236ac7421511"
                ],
                "parent": "540d4035-e461-4c95-acb3-0f1f187c374d",
                "msId": None,
                "groupId": None,
                "tenantId": None,
                "hwsStripe": None,
                "hwsAppSite": None,
                "hwsSwimLane": None,
                "hwsGroupName": None,
                "hwsSiteName": None,
                "hwsProduct": None,
                "logicSite": None,
                "hwsKind": "Application",
                "adminStatus": "Unlocked",
                "availableStatus": "Normal",
                "usageStatus": "Active",
                "operationalStatus": "Enabled",
                "language": None,
                "timeZone": None,
                "connectStatus": "NoDetect",
                "location": None,
                "maintainer": None,
                "contact": None,
                "deviceCode": None,
                "address": "",
                "code": "",
                "typeCode": "",
                "ipaddress": "",
                "hastatus": "Single",
            }
        ])

    # ---- /rest/dvpmservice/v1/openapi/monitor/history/data ----
    def do_POST_monitor_history(self):
        body = self._read_json()
        dn_map = body.get("dnOriginalValueMeasTypeCalTypes", {})
        time_ranges = body.get("timeRanges", {})
        start_ts = next(iter(time_ranges), 0)

        datas = []
        for dn in dn_map:
            ne_name = dn.rsplit("_", 1)[-1] if "_" in dn else dn
            min_rt = random.randint(0, 10)
            avg_rt = round(random.uniform(min_rt + 0.01, 50), 2)
            max_rt = random.randint(int(avg_rt) + 1, 200)
            total_count = random.randint(100, 500)
            success_count = random.randint(int(total_count * 0.8), total_count)
            success_ratio = round(success_count / total_count * 100, 2)
            datas.append({
                "dn": dn,
                "neName": ne_name,
                "timestamp": int(start_ts),
                "period": 300,
                "measObjects": {},
                "glitchMeasTypeKey": [],
                "values": {
                    "urlCluster_TotalSuccessResTime": "2067",
                    "urlCluster_MaxResTime": str(max_rt),
                    "urlCluster_TotalCount": str(total_count),
                    "urlCluster_Fail": "0",
                    "urlCluster_ResCodeOthers": "0",
                    "urlCluster_Success": str(success_count),
                    "urlCluster_MinResTime": str(min_rt),
                    "urlCluster_ResCode2xx": "219",
                    "urlCluster_ResCode1xx": "0",
                    "urlCluster_averageResTime": str(avg_rt),
                    "urlCluster_successRatio": str(success_ratio),
                    "urlCluster_ResCode4xx": "0",
                    "urlCluster_ResCode3xx": "0",
                    "urlCluster_TotalResTime": "2067",
                    "urlCluster_ResCode5xx": "0",
                },
            })

        self._send_json(200, {
            "resultCode": 0,
            "result": {
                "pageIndex": 1,
                "pageSize": len(datas),
                "totalCount": len(datas),
                "datas": datas,
            },
            "resultDesc": None,
        })

    # ---- /rest/fault/v1/current-alarms/scroll ----
    def do_POST_current_alarms_scroll(self):
        now_ms = str(int(time.time() * 1000))
        self._send_json(200, {
            "hits": [
                {
                    "severity": "2",
                    "meName": "bes_pr1_nslb-nginxshop-deploy-6fc786cd4d-rsfwq",
                    "alarmId": "888001307",
                    "count": str(random.randint(0, 10)),
                    "occurUtc": now_ms,
                    "alarmName": "Ratio of Successful Processed URL",
                    "nativeMeDn": "ab9902c3-e6c8-4d87-96b6-236ac7421511",
                    "moi": "Alarm Type=Fixed Threshold Alarm, Measurement unit=Nginx URL Kpi, Measurement counter=Ratio of Successful Processed URL, IP=192.171.1.123",
                    "additionalInformation": "Threshold Rule=Ratio of Successful Processed URL<=95%, Recover Value>=99.5%, and the current value=17.50%"
                }
            ],
            "iterator": "500&-1560524526#5aed4d1c-2d16-4e7f-94c5-069764b1efd6",
            "resMsg": "ok",
            "resCode": 1,
        })

    # ---- router ----
    def do_PUT(self):
        routes = {
            "/rest/plat/smapp/v1/sessions": self.do_PUT_sessions,
        }
        handler = routes.get(self.path.rstrip("/"))
        if handler:
            handler()
        else:
            self._send_json(404, {"error": f"PUT {self.path} not found"})

    def do_POST(self):
        routes = {
            "/rest/eammimservice/v1/openapi/mit/mos": self.do_POST_mos,
            "/rest/dvpmservice/v1/openapi/monitor/history/data": self.do_POST_monitor_history,
            "/rest/fault/v1/current-alarms/scroll": self.do_POST_current_alarms_scroll,
        }
        handler = routes.get(self.path.rstrip("/"))
        if handler:
            handler()
        else:
            self._send_json(404, {"error": f"POST {self.path} not found"})

    def do_GET(self):
        routes = {
            "/rest/eammimservice/v1/openapi/mit/mos": self.do_GET_mos,
        }
        handler = routes.get(self.path.rstrip("/"))
        if handler:
            handler()
        else:
            self._send_json(404, {"error": f"GET {self.path} not found"})

    def log_message(self, fmt, *args):
        # keep console output clean
        print(f"[mock] {args[0]}")


def _ensure_cert():
    cert_file = "mock_server.pem"
    key_file = "mock_server.key"
    if os.path.exists(cert_file) and os.path.exists(key_file):
        return cert_file, key_file
    print("Generating self-signed certificate ...")
    subprocess.run([
        "openssl", "req", "-x509", "-newkey", "rsa:2048",
        "-keyout", key_file, "-out", cert_file,
        "-days", "365", "-nodes",
        "-subj", "/CN=localhost",
    ], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    return cert_file, key_file


def main():
    cert_file, key_file = _ensure_cert()
    server = HTTPServer(("0.0.0.0", PORT), MockHandler)
    ctx = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE
    ctx.load_cert_chain(cert_file, key_file)
    server.socket = ctx.wrap_socket(server.socket, server_side=True)
    print(f"Mock server listening on https://0.0.0.0:{PORT}")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nShutting down.")
        server.server_close()


if __name__ == "__main__":
    main()
