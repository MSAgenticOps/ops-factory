/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test coverage for Command Whitelist Service.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class CommandWhitelistServiceTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private CommandWhitelistService whitelistService;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        GatewayProperties properties = new GatewayProperties();
        GatewayProperties.Paths paths = new GatewayProperties.Paths();
        paths.setProjectRoot(tempFolder.getRoot().getAbsolutePath());
        properties.setPaths(paths);

        whitelistService = new CommandWhitelistService(properties);
        whitelistService.init();
    }

    // ── init (default initialization) ────────────────────────────

    /**
     * Tests init creates default whitelist.
     */
    @Test
    public void testInit_createsDefaultWhitelist() throws Exception {
        // init() is called in setUp()
        Map<String, Object> whitelist = whitelistService.getWhitelist();
        assertNotNull(whitelist);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> commands = (List<Map<String, Object>>) whitelist.get("commands");
        assertNotNull(commands);
        assertFalse(commands.isEmpty());

        // Check that default commands are present
        Set<String> patterns = new HashSet<>();
        for (Map<String, Object> cmd : commands) {
            patterns.add((String) cmd.get("pattern"));
        }
        assertTrue(patterns.contains("ps"));
        assertTrue(patterns.contains("tail"));
        assertTrue(patterns.contains("grep"));
        assertTrue(patterns.contains("cat"));
        assertTrue(patterns.contains("ls"));
        assertTrue(patterns.contains("df"));
        assertTrue(patterns.contains("free"));
        assertTrue(patterns.contains("cd"));
    }

    // ── getWhitelist ─────────────────────────────────────────────

    /**
     * Tests get whitelist returns structure.
     */
    @Test
    public void testGetWhitelist_returnsStructure() throws Exception {
        Map<String, Object> whitelist = whitelistService.getWhitelist();
        assertTrue(whitelist.containsKey("commands"));
    }

    // ── addCommand ───────────────────────────────────────────────

    /**
     * Tests add command success.
     */
    @Test
    public void testAddCommand_success() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "iostat2");
        cmd.put("description", "查看IO统计");
        cmd.put("enabled", true);

        whitelistService.addCommand(cmd);

        Map<String, Object> whitelist = whitelistService.getWhitelist();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> commands = (List<Map<String, Object>>) whitelist.get("commands");
        boolean found = commands.stream().anyMatch(c -> "iostat2".equals(c.get("pattern")));
        assertTrue(found);
    }

    /**
     * Tests add command multiple.
     */
    @Test
    public void testAddCommand_multiple() throws Exception {
        Map<String, Object> cmd1 = new LinkedHashMap<>();
        cmd1.put("pattern", "cmd1");
        cmd1.put("enabled", true);
        whitelistService.addCommand(cmd1);

        Map<String, Object> cmd2 = new LinkedHashMap<>();
        cmd2.put("pattern", "cmd2");
        cmd2.put("enabled", true);
        whitelistService.addCommand(cmd2);

        Map<String, Object> whitelist = whitelistService.getWhitelist();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> commands = (List<Map<String, Object>>) whitelist.get("commands");
        assertTrue(commands.stream().anyMatch(c -> "cmd1".equals(c.get("pattern"))));
        assertTrue(commands.stream().anyMatch(c -> "cmd2".equals(c.get("pattern"))));
    }

    // ── updateCommand ────────────────────────────────────────────

    /**
     * Tests update command success.
     */
    @Test
    public void testUpdateCommand_success() throws Exception {
        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("description", "updated description");
        updates.put("enabled", false);

        whitelistService.updateCommand("ps", updates);

        Map<String, Object> whitelist = whitelistService.getWhitelist();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> commands = (List<Map<String, Object>>) whitelist.get("commands");
        Map<String, Object> psCmd =
            commands.stream().filter(c -> "ps".equals(c.get("pattern"))).findFirst().orElseThrow();
        assertEquals("updated description", psCmd.get("description"));
        assertEquals(false, psCmd.get("enabled"));
    }

    /**
     * Tests update command preserves pattern.
     */
    @Test
    public void testUpdateCommand_preservesPattern() throws Exception {
        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("description", "new desc");

        whitelistService.updateCommand("tail", updates);

        Map<String, Object> whitelist = whitelistService.getWhitelist();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> commands = (List<Map<String, Object>>) whitelist.get("commands");
        boolean tailExists = commands.stream().anyMatch(c -> "tail".equals(c.get("pattern")));
        assertTrue(tailExists);
    }

    /**
     * Tests update command not found.
     */
    @Test(expected = NotFoundException.class)
    public void testUpdateCommand_notFound() throws Exception {
        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("description", "test");
        whitelistService.updateCommand("nonexistent_cmd", updates);
    }

    // ── deleteCommand ────────────────────────────────────────────

    /**
     * Tests delete command success.
     */
    @Test
    public void testDeleteCommand_success() throws Exception {
        whitelistService.deleteCommand("ps");

        Map<String, Object> whitelist = whitelistService.getWhitelist();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> commands = (List<Map<String, Object>>) whitelist.get("commands");
        boolean psExists = commands.stream().anyMatch(c -> "ps".equals(c.get("pattern")));
        assertFalse(psExists);
    }

    /**
     * Tests delete command reduces count.
     */
    @Test
    public void testDeleteCommand_reducesCount() throws Exception {
        Map<String, Object> before = whitelistService.getWhitelist();
        @SuppressWarnings("unchecked")
        int countBefore = ((List<Map<String, Object>>) before.get("commands")).size();

        whitelistService.deleteCommand("grep");

        Map<String, Object> after = whitelistService.getWhitelist();
        @SuppressWarnings("unchecked")
        int countAfter = ((List<Map<String, Object>>) after.get("commands")).size();
        assertEquals(countBefore - 1, countAfter);
    }

    /**
     * Tests delete command not found.
     */
    @Test(expected = NotFoundException.class)
    public void testDeleteCommand_notFound() throws Exception {
        whitelistService.deleteCommand("nonexistent_cmd");
    }

    // ── validateCommand ──────────────────────────────────────────

    /**
     * Tests validate command all allowed.
     */
    @Test
    public void testValidateCommand_allAllowed() throws Exception {
        List<String> rejected = whitelistService.validateCommand("ps -ef");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command pipe allowed.
     */
    @Test
    public void testValidateCommand_pipeAllowed() throws Exception {
        List<String> rejected = whitelistService.validateCommand("ps -ef|grep java|grep -v grep");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command semicolon allowed.
     */
    @Test
    public void testValidateCommand_semicolonAllowed() throws Exception {
        List<String> rejected = whitelistService.validateCommand("cd /home;tail -n 50 log.txt");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command rejected command.
     */
    @Test
    public void testValidateCommand_rejectedCommand() throws Exception {
        List<String> rejected = whitelistService.validateCommand("rm -rf /");
        assertEquals(1, rejected.size());
        assertEquals("rm", rejected.get(0));
    }

    /**
     * Tests validate command mixed allowed and rejected.
     */
    @Test
    public void testValidateCommand_mixedAllowedAndRejected() throws Exception {
        List<String> rejected = whitelistService.validateCommand("ps -ef|reboot");
        assertEquals(1, rejected.size());
        assertEquals("reboot", rejected.get(0));
    }

    /**
     * Tests validate command disabled command.
     */
    @Test
    public void testValidateCommand_disabledCommand() throws Exception {
        // Disable 'ps' first
        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("enabled", false);
        whitelistService.updateCommand("ps", updates);

        List<String> rejected = whitelistService.validateCommand("ps -ef");
        assertEquals(1, rejected.size());
        assertEquals("ps", rejected.get(0));
    }

    /**
     * Tests validate command empty string.
     */
    @Test
    public void testValidateCommand_emptyString() throws Exception {
        List<String> rejected = whitelistService.validateCommand("");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command complex pipe.
     */
    @Test
    public void testValidateCommand_complexPipe() throws Exception {
        List<String> rejected =
            whitelistService.validateCommand("cd /home/rcpa/logs/stat;tail -n 50 pool.log|grep -v timeout");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command multiple rejected.
     */
    @Test
    public void testValidateCommand_multipleRejected() throws Exception {
        List<String> rejected = whitelistService.validateCommand("rm -rf /;reboot;shutdown now");
        assertEquals(3, rejected.size());
        assertTrue(rejected.contains("rm"));
        assertTrue(rejected.contains("reboot"));
        assertTrue(rejected.contains("shutdown"));
    }

    // ── validateCommand – pipes inside quotes (bug fix) ──────────

    /**
     * Tests validate command pipe inside single quotes.
     */
    @Test
    public void testValidateCommand_pipeInsideSingleQuotes() throws Exception {
        List<String> rejected = whitelistService
            .validateCommand("tail -100 /var/log/syslog | grep -E 'ERROR|WARN|Exception|Timeout' | tail -30");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command pipe inside double quotes.
     */
    @Test
    public void testValidateCommand_pipeInsideDoubleQuotes() throws Exception {
        List<String> rejected = whitelistService.validateCommand("grep \"ERROR|WARN\" /var/log/syslog | tail -20");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command escaped pipe.
     */
    @Test
    public void testValidateCommand_escapedPipe() throws Exception {
        List<String> rejected = whitelistService.validateCommand("grep 'a\\|b' /var/log/syslog | tail -20");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command mixed quotes and pipes.
     */
    @Test
    public void testValidateCommand_mixedQuotesAndPipes() throws Exception {
        List<String> rejected = whitelistService.validateCommand("grep -E 'ERROR|WARN' /var/log/syslog | rm -rf /");
        assertEquals(1, rejected.size());
        assertEquals("rm", rejected.get(0));
    }

    // ── getRiskLevel – pipes inside quotes ────────────────────────

    /**
     * Tests get risk level pipe inside quotes.
     */
    @Test
    public void testGetRiskLevel_pipeInsideQuotes() throws Exception {
        String risk = whitelistService.getRiskLevel("tail -100 /var/log/syslog | grep -E 'ERROR|WARN' | tail -30");
        assertEquals("low", risk);
    }

    // ── validateCommand – || and && operators ────────────────────

    /**
     * Tests validate command logical or.
     */
    @Test
    public void testValidateCommand_logicalOr() throws Exception {
        List<String> rejected = whitelistService.validateCommand("ps -ef || echo \"failed\"");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command logical and.
     */
    @Test
    public void testValidateCommand_logicalAnd() throws Exception {
        List<String> rejected = whitelistService.validateCommand("cd /home && ls -la");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests validate command logical or with rejected.
     */
    @Test
    public void testValidateCommand_logicalOrWithRejected() throws Exception {
        List<String> rejected = whitelistService.validateCommand("ps -ef || rm -rf /");
        assertEquals(1, rejected.size());
        assertEquals("rm", rejected.get(0));
    }

    /**
     * Tests validate command logical and with rejected.
     */
    @Test
    public void testValidateCommand_logicalAndWithRejected() throws Exception {
        List<String> rejected = whitelistService.validateCommand("cd /home && rm -rf /");
        assertEquals(1, rejected.size());
        assertEquals("rm", rejected.get(0));
    }

    /**
     * Tests validate command or or not split as two pipes.
     */
    @Test
    public void testValidateCommand_orOrNotSplitAsTwoPipes() throws Exception {
        // || should split into exactly 2 parts, not 3
        List<String> rejected = whitelistService.validateCommand("ps -ef || echo done");
        // If || were split as two |, echo would be in a separate segment but gmstat/echo both pass
        // The key is that || produces exactly 2 subcommands, not 3
        assertTrue(rejected.isEmpty());
    }

    // ── validateCommand – echo in default whitelist ──────────────

    /**
     * Tests validate command echo in default.
     */
    @Test
    public void testValidateCommand_echoInDefault() throws Exception {
        List<String> rejected = whitelistService.validateCommand("echo hello");
        assertTrue(rejected.isEmpty());
    }

    // ── Prefix matching mode (command body + arguments) ─────────

    /**
     * Tests prefix mode simple with absolute path.
     */
    @Test
    public void testPrefixMode_simpleWithAbsolutePath() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("/home/nslb");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests prefix mode simple with absolute path and args.
     */
    @Test
    public void testPrefixMode_simpleWithAbsolutePathAndArgs() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("/home/nslb list -v");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests prefix mode simple with relative path.
     */
    @Test
    public void testPrefixMode_simpleWithRelativePath() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("./nslb collect");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests prefix mode exact match.
     */
    @Test
    public void testPrefixMode_exactMatch() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("nslb list");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests prefix mode with extra args.
     */
    @Test
    public void testPrefixMode_withExtraArgs() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("nslb list -v");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests prefix mode different args rejected.
     */
    @Test
    public void testPrefixMode_differentArgs_rejected() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("nslb collect");
        assertEquals(1, rejected.size());
        assertEquals("nslb", rejected.get(0));
    }

    /**
     * Tests prefix mode word boundary rejected.
     */
    @Test
    public void testPrefixMode_wordBoundary_rejected() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("nslb listx");
        assertEquals(1, rejected.size());
        assertEquals("nslb", rejected.get(0));
    }

    /**
     * Tests prefix mode with path.
     */
    @Test
    public void testPrefixMode_withPath() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("/home/nslb list");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests prefix mode with path different args rejected.
     */
    @Test
    public void testPrefixMode_withPathDifferentArgs_rejected() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        List<String> rejected = whitelistService.validateCommand("/home/nslb collect");
        assertEquals(1, rejected.size());
        assertEquals("/home/nslb", rejected.get(0));
    }

    /**
     * Tests prefix mode simple and prefix coexist.
     */
    @Test
    public void testPrefixMode_simpleAndPrefixCoexist() throws Exception {
        Map<String, Object> cmd1 = new LinkedHashMap<>();
        cmd1.put("pattern", "nslb");
        cmd1.put("enabled", true);
        cmd1.put("riskLevel", "medium");
        whitelistService.addCommand(cmd1);

        Map<String, Object> cmd2 = new LinkedHashMap<>();
        cmd2.put("pattern", "nslb list");
        cmd2.put("enabled", true);
        cmd2.put("riskLevel", "low");
        whitelistService.addCommand(cmd2);

        // nslb collect matches the simple pattern "nslb"
        List<String> rejected = whitelistService.validateCommand("nslb collect");
        assertTrue(rejected.isEmpty());
    }

    /**
     * Tests prefix mode only prefix no simple.
     */
    @Test
    public void testPrefixMode_onlyPrefix_noSimple() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        // nslb collect does NOT match prefix pattern "nslb list"
        List<String> rejected = whitelistService.validateCommand("nslb collect");
        assertEquals(1, rejected.size());
    }

    // ── getRiskLevel – prefix matching ──────────────────────────

    /**
     * Tests get risk level prefix mode low.
     */
    @Test
    public void testGetRiskLevel_prefixModeLow() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        assertEquals("low", whitelistService.getRiskLevel("nslb list"));
    }

    /**
     * Tests get risk level prefix mode no match.
     */
    @Test
    public void testGetRiskLevel_prefixModeNoMatch() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        assertEquals("high", whitelistService.getRiskLevel("nslb collect"));
    }

    /**
     * Tests get risk level longer pattern wins.
     */
    @Test
    public void testGetRiskLevel_longerPatternWins() throws Exception {
        // When both "nslb"(medium) and "nslb list"(low) match, the longer pattern wins → low
        Map<String, Object> cmd1 = new LinkedHashMap<>();
        cmd1.put("pattern", "nslb");
        cmd1.put("enabled", true);
        cmd1.put("riskLevel", "medium");
        whitelistService.addCommand(cmd1);

        Map<String, Object> cmd2 = new LinkedHashMap<>();
        cmd2.put("pattern", "nslb list");
        cmd2.put("enabled", true);
        cmd2.put("riskLevel", "low");
        whitelistService.addCommand(cmd2);

        assertEquals("low", whitelistService.getRiskLevel("nslb list"));
    }

    /**
     * Tests get risk level prefix mode with path.
     */
    @Test
    public void testGetRiskLevel_prefixModeWithPath() throws Exception {
        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("pattern", "nslb list");
        cmd.put("enabled", true);
        cmd.put("riskLevel", "low");
        whitelistService.addCommand(cmd);

        assertEquals("low", whitelistService.getRiskLevel("/home/nslb list"));
    }
}
