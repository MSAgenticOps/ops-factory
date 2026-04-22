package com.huawei.opsfactory.skillmarket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.huawei.opsfactory.skillmarket.config.SkillMarketProperties;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.DefaultApplicationArguments;

class SkillMarketSeederTest {

    @TempDir
    Path tempDir;

    private SkillMarketProperties properties;
    private SkillCatalogService catalogService;
    private SkillMarketSeeder seeder;

    @BeforeEach
    void setUp() {
        properties = new SkillMarketProperties();
        properties.getRuntime().setBaseDir(tempDir.resolve("data").toString());
        catalogService = new SkillCatalogService(properties);
        seeder = new SkillMarketSeeder(catalogService, properties);
    }

    @Test
    void initializesFiveOpsSeedSkillsOnce() throws Exception {
        seeder.run(new DefaultApplicationArguments());

        assertEquals(5, catalogService.listSkills("").size());
        assertTrue(Files.exists(tempDir.resolve("data/.seeded-ops-skills")));
        assertTrue(Files.isRegularFile(tempDir.resolve("data/skills/incident-triage/unpacked/SKILL.md")));
        assertTrue(Files.isRegularFile(tempDir.resolve("data/skills/kubernetes-pod-recovery/package.zip")));

        seeder.run(new DefaultApplicationArguments());

        assertEquals(5, catalogService.listSkills("").size());
    }

    @Test
    void retriesMissingSeedSkillsEvenWhenMarkerExists() throws Exception {
        seeder.run(new DefaultApplicationArguments());
        catalogService.deleteSkill("incident-triage");

        assertEquals(4, catalogService.listSkills("").size());

        seeder.run(new DefaultApplicationArguments());

        assertEquals(5, catalogService.listSkills("").size());
        assertTrue(Files.isRegularFile(tempDir.resolve("data/skills/incident-triage/unpacked/SKILL.md")));
    }
}
