package com.huawei.opsfactory.skillmarket.model;

import java.util.List;

public record SkillListResponse(
    List<SkillSummary> items,
    int total
) {
}
