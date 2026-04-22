package com.huawei.opsfactory.skillmarket.api;

import com.huawei.opsfactory.skillmarket.config.SkillMarketProperties;
import com.huawei.opsfactory.skillmarket.model.CapabilitiesResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skill-market")
public class SystemController {

    private final SkillMarketProperties properties;

    public SystemController(SkillMarketProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/capabilities")
    public CapabilitiesResponse capabilities() {
        SkillMarketProperties.PackageSettings pack = properties.getPackage();
        return new CapabilitiesResponse(
            List.of("zip"),
            List.of("create", "import", "list", "detail", "download", "delete"),
            new CapabilitiesResponse.PackageLimits(
                pack.getMaxUploadSizeMb(),
                pack.getMaxUnpackedSizeMb(),
                pack.getMaxFileCount(),
                pack.getMaxSingleFileSizeMb(),
                pack.isAllowScripts()
            )
        );
    }
}
