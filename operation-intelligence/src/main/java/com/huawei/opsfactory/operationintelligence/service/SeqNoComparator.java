/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.service;

/**
 * Utility for comparing seqNo values with dot notation.
 *
 * @author call-chain
 * @since 2026-05-14
 */
public final class SeqNoComparator {

    private static final System.Logger log = System.getLogger(SeqNoComparator.class.getName());

    private SeqNoComparator() {}

    /**
     * Compare seqNo values with dot notation.
     *
     * @param s1 first seqNo
     * @param s2 second seqNo
     * @return comparison result
     */
    public static int compareSeqNo(String s1, String s2) {
        if (s1 == null)
            s1 = "0";
        if (s2 == null)
            s2 = "0";

        String[] parts1 = s1.split("\\.");
        String[] parts2 = s2.split("\\.");

        int len = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < len; i++) {
            int v1 = i < parts1.length ? parseSeqNoPart(parts1[i]) : 0;
            int v2 = i < parts2.length ? parseSeqNoPart(parts2[i]) : 0;
            if (v1 != v2) {
                return Integer.compare(v1, v2);
            }
        }
        return 0;
    }

    /**
     * Parse a single seqNo part to integer.
     *
     * @param part the seqNo part
     * @return the integer value
     */
    public static int parseSeqNoPart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            log.log(System.Logger.Level.DEBUG, "Non-numeric seqNo part '{0}', treating as 0", part);
            return 0;
        }
    }
}
