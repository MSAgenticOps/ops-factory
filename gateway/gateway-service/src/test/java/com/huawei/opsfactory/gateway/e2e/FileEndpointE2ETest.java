package com.huawei.opsfactory.gateway.e2e;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * E2E tests for FileController endpoints:
 * GET /agents/{agentId}/files
 * GET /agents/{agentId}/files/**
 * PUT /agents/{agentId}/files/**
 * DELETE /agents/{agentId}/files/**
 * POST /agents/{agentId}/files/upload
 * @author x00000000
 * @since 2026-05-09
 */
public class FileEndpointE2ETest extends BaseE2ETest {
    private static final Path USERS_DIR = Path.of("/tmp/test-gateway/gateway/users");

    @Before
    public void setUp() {
        when(agentConfigService.getUserAgentDir(any(String.class), any(String.class)))
                .thenAnswer(inv -> USERS_DIR.resolve(inv.getArgument(0, String.class))
                        .resolve("agents").resolve(inv.getArgument(1, String.class)));
    }

    // ====================== GET /agents/{agentId}/files ======================

    @Test
    public void listFiles_authenticated_returnsFileList() throws IOException {
        when(fileService.listFiles(any(Path.class))).thenReturn(List.of(
                Map.of("name", "report.pdf", "path", "data/report.pdf", "size", 1024),
                Map.of("name", "notes.txt", "path", "data/notes.txt", "size", 256)));

        webClient.get().uri("/gateway/agents/test-agent/files")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.files.length()").isEqualTo(2)
                .jsonPath("$.files[0].name").isEqualTo("report.pdf")
                .jsonPath("$.files[0].size").isEqualTo(1024)
                .jsonPath("$.files[1].name").isEqualTo("notes.txt");
    }

    @Test
    public void listFiles_emptyDir_returnsEmptyArray() throws IOException {
        when(fileService.listFiles(any(Path.class))).thenReturn(Collections.emptyList());

        webClient.get().uri("/gateway/agents/test-agent/files")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.files.length()").isEqualTo(0);
    }

    @Test
    public void listFiles_unauthenticated_returns401() {
        webClient.get().uri("/gateway/agents/test-agent/files")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void listFiles_ioException_returns500() throws IOException {
        when(fileService.listFiles(any(Path.class))).thenThrow(new IOException("disk error"));

        webClient.get().uri("/gateway/agents/test-agent/files")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // ====================== GET /agents/{agentId}/files/** (Download) ======================

    @Test
    public void getFile_existingTextFile_returnsInlineContent() {
        ByteArrayResource resource = new ByteArrayResource("Hello World".getBytes()) {
            @Override
            public String getFilename() {
                return "readme.txt";
            }
        };
        when(fileService.resolveFile(any(Path.class), eq("data/readme.txt")))
                .thenReturn(resource);
        when(fileService.getMimeType("readme.txt")).thenReturn("text/plain");
        when(fileService.isInline("text/plain")).thenReturn(true);

        webClient.get().uri("/gateway/agents/test-agent/files/data/readme.txt")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/plain")
                .expectHeader().valueMatches("Content-Disposition", "inline.*readme\\.txt.*");
    }

    @Test
    public void getFile_binaryFile_returnsAsAttachment() {
        ByteArrayResource resource = new ByteArrayResource(new byte[]{0x50, 0x4B}) {
            @Override
            public String getFilename() {
                return "archive.zip";
            }
        };
        when(fileService.resolveFile(any(Path.class), eq("archive.zip")))
                .thenReturn(resource);
        when(fileService.getMimeType("archive.zip")).thenReturn("application/zip");
        when(fileService.isInline("application/zip")).thenReturn(false);

        webClient.get().uri("/gateway/agents/test-agent/files/archive.zip")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/zip")
                .expectHeader().valueMatches("Content-Disposition", "attachment.*archive\\.zip.*");
    }

    @Test
    public void getFile_notFound_returns404() {
        when(fileService.resolveFile(any(Path.class), eq("nonexistent.txt")))
                .thenReturn(null);

        webClient.get().uri("/gateway/agents/test-agent/files/nonexistent.txt")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getFile_unauthenticated_returns401() {
        webClient.get().uri("/gateway/agents/test-agent/files/data/secret.txt")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void getFile_nestedPath_resolvesCorrectly() {
        ByteArrayResource resource = new ByteArrayResource("nested".getBytes()) {
            @Override
            public String getFilename() {
                return "nested.txt";
            }
        };
        when(fileService.resolveFile(any(Path.class), eq("data/subdir/nested.txt")))
                .thenReturn(resource);
        when(fileService.getMimeType("nested.txt")).thenReturn("text/plain");
        when(fileService.isInline("text/plain")).thenReturn(true);

        webClient.get().uri("/gateway/agents/test-agent/files/data/subdir/nested.txt")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getFile_rootId_resolvesFromScanRoot() {
        Path userAgentDir = USERS_DIR.resolve("alice").resolve("agents").resolve("test-agent");
        Path outputDir = userAgentDir.resolve("output");
        ByteArrayResource resource = new ByteArrayResource("output".getBytes()) {
            @Override
            public String getFilename() {
                return "report.md";
            }
        };
        when(fileService.resolveFileScanRoot(userAgentDir, "output")).thenReturn(Optional.of(outputDir));
        when(fileService.resolveFile(outputDir, "report.md")).thenReturn(resource);
        when(fileService.getMimeType("report.md")).thenReturn("text/markdown");
        when(fileService.isInline("text/markdown")).thenReturn(true);

        webClient.get().uri("/gateway/agents/test-agent/files/report.md?rootId=output")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/markdown");
    }

    // ====================== PUT /agents/{agentId}/files/** ======================

    @Test
    public void updateFile_existingTextFile_returnsUpdated() throws IOException {
        when(fileService.isEditableTextFile("data/readme.md")).thenReturn(true);
        when(fileService.updateTextFile(any(Path.class), eq("data/readme.md"), eq("# Updated")))
                .thenReturn(true);

        webClient.put().uri("/gateway/agents/test-agent/files/data/readme.md")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("content", "# Updated"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("updated")
                .jsonPath("$.path").isEqualTo("data/readme.md");
    }

    @Test
    public void updateFile_notFound_returns404() throws IOException {
        when(fileService.isEditableTextFile("data/missing.txt")).thenReturn(true);
        when(fileService.updateTextFile(any(Path.class), eq("data/missing.txt"), eq("Updated")))
                .thenReturn(false);

        webClient.put().uri("/gateway/agents/test-agent/files/data/missing.txt")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("content", "Updated"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("file not found");
    }

    @Test
    public void updateFile_unsupportedType_returns415() {
        when(fileService.isEditableTextFile("deck.pptx")).thenReturn(false);

        webClient.put().uri("/gateway/agents/test-agent/files/deck.pptx")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("content", "Updated"))
                .exchange()
                .expectStatus().isEqualTo(415)
                .expectBody()
                .jsonPath("$.error").isEqualTo("file type is not editable");
    }

    @Test
    public void updateFile_pathTraversal_returns403() {
        webClient.put().uri("/gateway/agents/test-agent/files/..%2F..%2Fsecret.txt")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("content", "Updated"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.error").isEqualTo("path traversal not allowed");
    }

    @Test
    public void updateFile_unauthenticated_returns401() {
        webClient.put().uri("/gateway/agents/test-agent/files/data/readme.txt")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("content", "Updated"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ====================== DELETE /agents/{agentId}/files/** ======================

    @Test
    public void deleteFile_existingFile_returnsDeleted() throws IOException {
        when(fileService.deleteFile(any(Path.class), eq("data/readme.txt"))).thenReturn(true);

        webClient.delete().uri("/gateway/agents/test-agent/files/data/readme.txt")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("deleted")
                .jsonPath("$.path").isEqualTo("data/readme.txt");
    }

    @Test
    public void deleteFile_notFound_returns404() throws IOException {
        when(fileService.deleteFile(any(Path.class), eq("data/missing.txt"))).thenReturn(false);

        webClient.delete().uri("/gateway/agents/test-agent/files/data/missing.txt")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("file not found");
    }

    @Test
    public void deleteFile_pathTraversal_returns403() {
        webClient.delete().uri("/gateway/agents/test-agent/files/..%2F..%2Fsecret.txt")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.error").isEqualTo("path traversal not allowed");
    }

    @Test
    public void deleteFile_unauthenticated_returns401() {
        webClient.delete().uri("/gateway/agents/test-agent/files/data/readme.txt")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ====================== POST /agents/{agentId}/files/upload ======================

    // Note: Upload testing with multipart in WebTestClient requires special setup.
    // These tests verify auth and routing, not actual file transfer.

    @Test
    public void uploadFile_unauthenticated_returns401() {
        webClient.post().uri("/gateway/agents/test-agent/files/upload?sessionId=s1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ====================== User isolation ======================

    @Test
    public void listFiles_differentUsers_resolveDifferentPaths() throws IOException {
        when(fileService.listFiles(USERS_DIR.resolve("alice").resolve("agents").resolve("test-agent")))
                .thenReturn(List.of(Map.of("name", "alice-file.txt", "path", "alice-file.txt", "size", 100)));
        when(fileService.listFiles(USERS_DIR.resolve("bob").resolve("agents").resolve("test-agent")))
                .thenReturn(List.of(Map.of("name", "bob-file.txt", "path", "bob-file.txt", "size", 200)));

        // Alice sees her files
        webClient.get().uri("/gateway/agents/test-agent/files")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "alice")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.files[0].name").isEqualTo("alice-file.txt");

        // Bob sees his files
        webClient.get().uri("/gateway/agents/test-agent/files")
                .header(HEADER_SECRET_KEY, SECRET_KEY)
                .header(HEADER_USER_ID, "bob")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.files[0].name").isEqualTo("bob-file.txt");
    }
}
