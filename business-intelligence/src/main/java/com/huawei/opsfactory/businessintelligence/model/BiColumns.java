package com.huawei.opsfactory.businessintelligence.model;

public final class BiColumns {

    // ── Incidents ──
    public static final String ORDER_NUMBER = "Order Number";
    public static final String PRIORITY = "Priority";
    public static final String CATEGORY = "Category";
    public static final String ORDER_STATUS = "Order Status";
    public static final String BEGIN_DATE = "Begin Date";
    public static final String END_DATE = "End Date";
    public static final String RESOLVER = "Resolver";
    public static final String RESPONDER = "Responder";
    public static final String RESPONSE_TIME_M = "Response Time(m)";
    public static final String RESOLUTION_TIME_M = "Resolution Time(m)";
    public static final String SLA_COMPLIANT = "SLA Compliant";
    public static final String CI_AFFECTED = "CI Affected";

    // ── Changes ──
    public static final String CHANGE_NUMBER = "Change Number";
    public static final String CHANGE_TYPE = "Change Type";
    public static final String SUCCESS = "Success";
    public static final String INCIDENT_CAUSED = "Incident Caused";
    public static final String REQUESTED_DATE = "Requested Date";
    public static final String PLANNED_START = "Planned Start";

    // ── Requests ──
    public static final String REQUEST_NUMBER = "Request Number";
    public static final String REQUEST_TYPE = "Request Type";
    public static final String REQUESTER_DEPT = "Requester Dept";
    public static final String FULFILLMENT_TIME_H = "Fulfillment Time(h)";
    public static final String SLA_MET = "SLA Met";
    public static final String SATISFACTION_SCORE = "Satisfaction Score";

    // ── Problems ──
    public static final String PROBLEM_NUMBER = "Problem Number";
    public static final String LOGGED_DATE = "Logged Date";
    public static final String ROOT_CAUSE_CATEGORY = "Root Cause Category";
    public static final String KNOWN_ERROR = "Known Error";
    public static final String WORKAROUND_AVAILABLE = "Workaround Available";

    // ── Common ──
    public static final String STATUS = "Status";
    public static final String CLOSED_DATE = "Closed Date";

    private BiColumns() {}
}
