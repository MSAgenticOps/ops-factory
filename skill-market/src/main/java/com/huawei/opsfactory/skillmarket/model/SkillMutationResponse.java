package com.huawei.opsfactory.skillmarket.model;

import java.util.List;

public record SkillMutationResponse(
    SkillSummary skill,
    List<SkillWarning> warnings
) {
}
