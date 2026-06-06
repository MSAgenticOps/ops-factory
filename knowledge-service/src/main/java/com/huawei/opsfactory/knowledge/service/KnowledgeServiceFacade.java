/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.knowledge.service;

import com.huawei.opsfactory.knowledge.api.chunk.ChunkController;
import com.huawei.opsfactory.knowledge.api.document.DocumentController;
import com.huawei.opsfactory.knowledge.api.job.JobController;
import com.huawei.opsfactory.knowledge.api.profile.ProfileController;
import com.huawei.opsfactory.knowledge.api.retrieval.RetrievalController;
import com.huawei.opsfactory.knowledge.api.source.SourceController;
import com.huawei.opsfactory.knowledge.common.error.ApiConflictException;
import com.huawei.opsfactory.knowledge.common.logging.LoggingKeys;
import com.huawei.opsfactory.knowledge.common.model.PageResponse;
import com.huawei.opsfactory.knowledge.common.util.Ids;
import com.huawei.opsfactory.knowledge.config.KnowledgeLoggingProperties;
import com.huawei.opsfactory.knowledge.config.KnowledgeProperties;
import com.huawei.opsfactory.knowledge.repository.BindingRepository;
import com.huawei.opsfactory.knowledge.repository.ChunkRepository;
import com.huawei.opsfactory.knowledge.repository.DocumentRepository;
import com.huawei.opsfactory.knowledge.repository.JobRepository;
import com.huawei.opsfactory.knowledge.repository.MaintenanceJobFailureRepository;
import com.huawei.opsfactory.knowledge.repository.ProfileRepository;
import com.huawei.opsfactory.knowledge.repository.SourceRepository;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Facade service for knowledge management, coordinating sources, documents,
 * chunks, profiles, search/retrieval, and job operations.
 *
 * @author x00000000
 * @since 2026-05-26
 */
@Service
public class KnowledgeServiceFacade {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeServiceFacade.class);
    private static final int COMPARE_FETCH_TOP_K = 64;
    private static final Set<String> GENERIC_CONTENT_TYPES = Set.of("application/octet-stream");
    private static final Set<String> CHM_CONTENT_TYPES = Set.of(
        "application/vnd.ms-htmlhelp",
        "application/chm",
        "application/x-chm"
    );

    private final SourceRepository sourceRepository;
    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final JobRepository jobRepository;
    private final MaintenanceJobFailureRepository maintenanceJobFailureRepository;
    private final ProfileRepository profileRepository;
    private final BindingRepository bindingRepository;
    private final StorageManager storageManager;
    private final TikaConversionService conversionService;
    private final ChunkingService chunkingService;
    private final SearchService searchService;
    private final EmbeddingService embeddingService;
    private final LexicalIndexService lexicalIndexService;
    private final VectorIndexService vectorIndexService;
    private final ProfileBootstrapService profileBootstrapService;
    private final KnowledgeLoggingProperties knowledgeLoggingProperties;
    private final ThreadPoolTaskExecutor taskExecutor;

    /**
     * Constructs a new {@code KnowledgeServiceFacade} with all required dependencies.
     *
     * @param sourceRepository the source repository
     * @param documentRepository the document repository
     * @param chunkRepository the chunk repository
     * @param jobRepository the job repository
     * @param maintenanceJobFailureRepository the maintenance job failure repository
     * @param profileRepository the profile repository
     * @param bindingRepository the binding repository
     * @param storageManager the storage manager
     * @param conversionService the conversion service
     * @param chunkingService the chunking service
     * @param searchService the search service
     * @param embeddingService the embedding service
     * @param lexicalIndexService the lexical index service
     * @param vectorIndexService the vector index service
     * @param profileBootstrapService the profile bootstrap service
     * @param knowledgeLoggingProperties the knowledge logging properties
     * @param taskExecutor the task executor
     */
    public KnowledgeServiceFacade(
        SourceRepository sourceRepository,
        DocumentRepository documentRepository,
        ChunkRepository chunkRepository,
        JobRepository jobRepository,
        MaintenanceJobFailureRepository maintenanceJobFailureRepository,
        ProfileRepository profileRepository,
        BindingRepository bindingRepository,
        StorageManager storageManager,
        TikaConversionService conversionService,
        ChunkingService chunkingService,
        SearchService searchService,
        EmbeddingService embeddingService,
        LexicalIndexService lexicalIndexService,
        VectorIndexService vectorIndexService,
        ProfileBootstrapService profileBootstrapService,
        KnowledgeLoggingProperties knowledgeLoggingProperties,
        ThreadPoolTaskExecutor taskExecutor
    ) {
        this.sourceRepository = sourceRepository;
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.jobRepository = jobRepository;
        this.maintenanceJobFailureRepository = maintenanceJobFailureRepository;
        this.profileRepository = profileRepository;
        this.bindingRepository = bindingRepository;
        this.storageManager = storageManager;
        this.conversionService = conversionService;
        this.chunkingService = chunkingService;
        this.searchService = searchService;
        this.embeddingService = embeddingService;
        this.lexicalIndexService = lexicalIndexService;
        this.vectorIndexService = vectorIndexService;
        this.profileBootstrapService = profileBootstrapService;
        this.knowledgeLoggingProperties = knowledgeLoggingProperties;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Lists all knowledge sources with pagination.
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return a paginated response of source summaries
     */
    public PageResponse<SourceController.SourceResponse> listSources(int page, int pageSize) {
        List<SourceController.SourceResponse> items = sourceRepository.findAll().stream().map(this::toSourceResponse).toList();
        return page(items, page, pageSize);
    }

    /**
     * Creates a new knowledge source with default index and retrieval profiles.
     * @param request the create source request containing name and description
     * @return the created source response
     * @throws IllegalArgumentException if bound profiles are not found
     */
    @Transactional
    public SourceController.SourceResponse createSource(SourceController.CreateSourceRequest request) {
        Instant now = Instant.now();
        String id = Ids.newId("src");
        String indexProfileId = request.indexProfileId() != null ? request.indexProfileId() : profileBootstrapService.defaultIndexProfileId();
        String retrievalProfileId = request.retrievalProfileId() != null ? request.retrievalProfileId() : profileBootstrapService.defaultRetrievalProfileId();
        validateIndexProfileBindableToSource(indexProfileId, id);
        validateRetrievalProfileBindableToSource(retrievalProfileId, id);
        SourceRepository.SourceRecord record = new SourceRepository.SourceRecord(
            id, request.name(), request.description(), "ACTIVE", "MANAGED", indexProfileId, retrievalProfileId,
            "ACTIVE", null, null, null, false, now, now
        );
        sourceRepository.insert(record);
        bindingRepository.upsert(new BindingRepository.BindingRecord(Ids.newId("spb"), id, indexProfileId, retrievalProfileId, now, now));
        log.info("Created source sourceId={} name={} indexProfileId={} retrievalProfileId={}", id, request.name(), indexProfileId, retrievalProfileId);
        return toSourceResponse(record);
    }

    /**
     * Retrieves a single knowledge source by its identifier.
     * @param sourceId the source identifier
     * @return the source response
     * @throws IllegalArgumentException if the source is not found
     */
    public SourceController.SourceResponse getSource(String sourceId) {
        return sourceRepository.findById(sourceId).map(this::toSourceResponse)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
    }

    /**
     * Updates an existing knowledge source.
     * @param sourceId the source identifier
     * @param request the update request containing optional name, description, status, and profile bindings
     * @return the updated source response
     * @throws IllegalArgumentException if the source or bound profiles are not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public SourceController.SourceResponse updateSource(String sourceId, SourceController.UpdateSourceRequest request) {
        SourceRepository.SourceRecord existing = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ensureSourceWritable(existing);
        Instant now = Instant.now();
        String indexProfileId = request.indexProfileId() != null ? request.indexProfileId() : existing.indexProfileId();
        String retrievalProfileId = request.retrievalProfileId() != null ? request.retrievalProfileId() : existing.retrievalProfileId();
        validateIndexProfileBindableToSource(indexProfileId, sourceId);
        validateRetrievalProfileBindableToSource(retrievalProfileId, sourceId);
        SourceRepository.SourceRecord updated = new SourceRepository.SourceRecord(
            sourceId,
            request.name() != null ? request.name() : existing.name(),
            request.description() != null ? request.description() : existing.description(),
            request.status() != null ? request.status() : existing.status(),
            existing.storageMode(),
            indexProfileId,
            retrievalProfileId,
            existing.runtimeStatus(),
            existing.runtimeMessage(),
            existing.currentJobId(),
            existing.lastJobError(),
            existing.rebuildRequired() || !indexProfileId.equals(existing.indexProfileId()),
            existing.createdAt(),
            now
        );
        sourceRepository.update(updated);
        bindingRepository.upsert(new BindingRepository.BindingRecord(
            Ids.newId("spb"), sourceId, updated.indexProfileId(), updated.retrievalProfileId(), existing.createdAt(), now
        ));
        log.info(
            "Updated source sourceId={} status={} indexProfileId={} retrievalProfileId={} rebuildRequired={}",
            sourceId,
            updated.status(),
            updated.indexProfileId(),
            updated.retrievalProfileId(),
            updated.rebuildRequired()
        );
        return toSourceResponse(updated);
    }

    /**
     * Deletes a knowledge source and all its associated data.
     * @param sourceId the source identifier
     * @return the deletion response
     * @throws IllegalArgumentException if the source is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public SourceController.DeleteSourceResponse deleteSource(String sourceId) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ensureSourceWritable(source);
        List<DocumentRepository.DocumentRecord> documents = documentRepository.findBySourceId(sourceId);
        for (DocumentRepository.DocumentRecord document : documents) {
            storageManager.deleteRecursively(storageManager.artifactDir(sourceId, document.id()));
            storageManager.deleteRecursively(storageManager.uploadDocumentDir(sourceId, document.id()));
        }
        lexicalIndexService.deleteSource(sourceId);
        vectorIndexService.deleteSource(sourceId);
        chunkRepository.deleteBySourceId(sourceId);
        documentRepository.deleteBySourceId(sourceId);
        jobRepository.deleteBySourceId(sourceId);
        bindingRepository.deleteBySourceId(sourceId);
        sourceRepository.delete(source.id());
        storageManager.deleteRecursively(storageManager.artifactSourceDir(sourceId));
        storageManager.deleteRecursively(storageManager.uploadSourceDir(sourceId));
        log.info("Deleted source sourceId={} removedDocuments={}", sourceId, documents.size());
        return new SourceController.DeleteSourceResponse(sourceId, true);
    }

    /**
     * Retrieves statistics for a knowledge source.
     * @param sourceId the source identifier
     * @return the source statistics response
     */
    public SourceController.SourceStatsResponse sourceStats(String sourceId) {
        long documentCount = documentRepository.findBySourceId(sourceId).size();
        long indexedCount = documentRepository.findBySourceId(sourceId).stream().filter(d -> "INDEXED".equals(d.status())).count();
        long failedCount = documentRepository.findBySourceId(sourceId).stream().filter(d -> "ERROR".equals(d.status())).count();
        long processingCount = documentRepository.findBySourceId(sourceId).stream().filter(d -> "PROCESSING".equals(d.status())).count();
        long chunkCount = chunkRepository.countBySourceId(sourceId);
        long userEditedCount = chunkRepository.countUserEditedBySourceId(sourceId);
        Instant lastIngestion = jobRepository.findAll().stream()
            .filter(j -> sourceId.equals(j.sourceId()) && "SUCCEEDED".equals(j.status()))
            .map(JobRepository.JobRecord::updatedAt)
            .max(Comparator.naturalOrder())
            .orElse(null);
        return new SourceController.SourceStatsResponse(
            sourceId, (int) documentCount, (int) indexedCount, (int) failedCount, (int) processingCount,
            (int) chunkCount, (int) userEditedCount, lastIngestion
        );
    }

    /**
     * Lists documents with optional source filtering and pagination.
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @param sourceId optional source identifier to filter by; null to list all documents
     * @return a paginated response of document summaries
     */
    public PageResponse<DocumentController.DocumentSummary> listDocuments(int page, int pageSize, String sourceId) {
        List<DocumentRepository.DocumentRecord> docs = sourceId == null ? documentRepository.findAll() : documentRepository.findBySourceId(sourceId);
        List<DocumentController.DocumentSummary> items = docs.stream().map(this::toDocumentSummary).toList();
        return page(items, page, pageSize);
    }

    /**
     * Ingests files into a knowledge source, converting and chunking them.
     * @param sourceId the source identifier
     * @param files the files to ingest
     * @return the ingest response containing job id, imported count, and skipped files
     * @throws IllegalArgumentException if the source is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     * @throws RuntimeException if ingestion fails
     */
    public DocumentController.IngestDocumentsResponse ingest(String sourceId, MultipartFile[] files) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ensureSourceWritable(source);
        Instant now = Instant.now();
        JobRepository.JobRecord job = new JobRepository.JobRecord(Ids.newId("job"), "INGEST", sourceId, null, "RUNNING", 0, null, "Ingest started", "system", 0, 0, 0, 0, null, null, null, now, null, now, now);
        jobRepository.insert(job);
        int imported = 0;
        List<DocumentController.SkippedFileInfo> skipped = new ArrayList<>();
        long startedAt = System.currentTimeMillis();
        putMdcIfText(LoggingKeys.SOURCE_ID, sourceId);
        putMdcIfText(LoggingKeys.JOB_ID, job.id());
        try {
            log.info("Starting ingest sourceId={} jobId={} fileCount={}", sourceId, job.id(), files == null ? 0 : files.length);
            for (MultipartFile file : files) {
                if (file.isEmpty() || !StringUtils.hasText(file.getOriginalFilename())) {
                    continue;
                }
                UploadResult result = processUploadWithResult(sourceId, file);
                if (result.imported()) {
                    imported++;
                } else if (result.skipReason() != null) {
                    skipped.add(new DocumentController.SkippedFileInfo(file.getOriginalFilename(), result.skipReason(), result.existingFileName()));
                }
            }
            JobRepository.JobRecord finished = new JobRepository.JobRecord(job.id(), job.jobType(), sourceId, null, "SUCCEEDED", 100, null, "Ingest completed", "system", 0, 0, 0, 0, null, null, null, now, Instant.now(), job.createdAt(), Instant.now());
            jobRepository.update(finished);
            log.info(
                "Completed ingest sourceId={} jobId={} imported={} skipped={} durationMs={}",
                sourceId,
                job.id(),
                imported,
                skipped.size(),
                System.currentTimeMillis() - startedAt
            );
            return new DocumentController.IngestDocumentsResponse(job.id(), sourceId, "SUCCEEDED", imported, skipped);
        } catch (RuntimeException ex) {
            JobRepository.JobRecord failed = new JobRepository.JobRecord(job.id(), job.jobType(), sourceId, null, "FAILED", imported == 0 ? 0 : 100, null, ex.getMessage(), "system", 0, 0, 0, 0, null, null, ex.getMessage(), now, Instant.now(), job.createdAt(), Instant.now());
            jobRepository.update(failed);
            log.error(
                "Failed ingest sourceId={} jobId={} imported={} skipped={} durationMs={}",
                sourceId,
                job.id(),
                imported,
                skipped.size(),
                System.currentTimeMillis() - startedAt,
                ex
            );
            throw ex;
        } finally {
            removeMdcIfText(LoggingKeys.JOB_ID, job.id());
            removeMdcIfText(LoggingKeys.SOURCE_ID, sourceId);
        }
    }

    /**
     * Retrieves a single document by its identifier.
     * @param documentId the document identifier
     * @return the document detail
     * @throws IllegalArgumentException if the document is not found
     */
    public DocumentController.DocumentDetail getDocument(String documentId) {
        return documentRepository.findById(documentId).map(this::toDocumentDetail)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
    }

    /**
     * Updates an existing document's metadata.
     * @param documentId the document identifier
     * @param request the update request containing optional title, description, and tags
     * @return the update response
     * @throws IllegalArgumentException if the document is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public DocumentController.DocumentUpdateResponse updateDocument(String documentId, DocumentController.UpdateDocumentRequest request) {
        DocumentRepository.DocumentRecord existing = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        ensureSourceWritable(existing.sourceId());
        DocumentRepository.DocumentRecord updated = new DocumentRepository.DocumentRecord(
            existing.id(), existing.sourceId(), existing.name(), existing.originalFilename(),
            request.title() != null ? request.title() : existing.title(),
            request.description() != null ? request.description() : existing.description(),
            request.tags() != null ? request.tags() : existing.tags(),
            existing.sha256(), existing.contentType(), existing.language(), existing.status(), existing.indexStatus(),
            existing.fileSizeBytes(), existing.chunkCount(), existing.userEditedChunkCount(), existing.errorMessage(),
            "system", existing.createdAt(), Instant.now()
        );
        documentRepository.update(updated);
        return new DocumentController.DocumentUpdateResponse(documentId, true, updated.updatedAt());
    }

    /**
     * Deletes a document and all its associated chunks and artifacts.
     * @param documentId the document identifier
     * @return the deletion response
     * @throws IllegalArgumentException if the document is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public DocumentController.DeleteDocumentResponse deleteDocument(String documentId) {
        DocumentRepository.DocumentRecord existing = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        ensureSourceWritable(existing.sourceId());
        lexicalIndexService.deleteDocument(existing.sourceId(), documentId);
        vectorIndexService.deleteDocument(documentId);
        chunkRepository.deleteByDocumentId(documentId);
        documentRepository.delete(documentId);
        storageManager.deleteRecursively(storageManager.artifactDir(existing.sourceId(), existing.id()));
        storageManager.deleteRecursively(storageManager.uploadDocumentDir(existing.sourceId(), existing.id()));
        return new DocumentController.DeleteDocumentResponse(documentId, true);
    }

    /**
     * Lists chunks for a specific document with pagination.
     * @param documentId the document identifier
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return a paginated response of chunk summaries
     */
    public PageResponse<ChunkController.ChunkSummary> listDocumentChunks(String documentId, int page, int pageSize) {
        List<ChunkController.ChunkSummary> items = chunkRepository.findByDocumentId(documentId).stream().map(this::toChunkSummary).toList();
        return page(items, page, pageSize);
    }

    /**
     * Generates a preview of a document's markdown content.
     * @param documentId the document identifier
     * @return the document preview response containing title and markdown text
     * @throws IllegalArgumentException if the document is not found
     */
    public DocumentController.DocumentPreviewResponse previewDocument(String documentId) {
        DocumentRepository.DocumentRecord document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        Path artifactDir = storageManager.artifactDir(document.sourceId(), document.id());
        return new DocumentController.DocumentPreviewResponse(
            documentId,
            document.title(),
            storageManager.readString(artifactDir.resolve("content.md"))
        );
    }

    /**
     * Checks whether a document has generated artifact files.
     * @param documentId the document identifier
     * @return the artifacts response indicating whether markdown artifact exists
     * @throws IllegalArgumentException if the document is not found
     */
    public DocumentController.DocumentArtifactsResponse getArtifacts(String documentId) {
        DocumentRepository.DocumentRecord document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        Path artifactDir = storageManager.artifactDir(document.sourceId(), document.id());
        return new DocumentController.DocumentArtifactsResponse(
            documentId,
            java.nio.file.Files.exists(artifactDir.resolve("content.md"))
        );
    }

    /**
     * Reads a named artifact file for a document.
     * @param documentId the document identifier
     * @param name the artifact file name
     * @return the artifact content as a string
     * @throws IllegalArgumentException if the document is not found
     */
    public String readArtifact(String documentId, String name) {
        DocumentRepository.DocumentRecord document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        return storageManager.readString(storageManager.artifactDir(document.sourceId(), document.id()).resolve(name));
    }

    /**
     * Retrieves the original uploaded file for a document.
     * @param documentId the document identifier
     * @return the original document response containing filename, content type, and bytes
     * @throws IllegalArgumentException if the document is not found
     */
    public DocumentController.OriginalDocumentResponse originalDocument(String documentId) {
        DocumentRepository.DocumentRecord document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        Path originalPath = storageManager.originalFilePath(document.sourceId(), document.id(), document.originalFilename());
        return new DocumentController.OriginalDocumentResponse(
            document.id(),
            document.originalFilename(),
            document.contentType(),
            storageManager.readBytes(originalPath)
        );
    }

    /**
     * Creates a simple completed job record for a document operation.
     * @param documentId the document identifier
     * @param jobType the job type string
     * @return the job creation response
     * @throws IllegalArgumentException if the document is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    public DocumentController.JobCreationResponse simpleDocumentJob(String documentId, String jobType) {
        DocumentRepository.DocumentRecord document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        ensureSourceWritable(document.sourceId());
        Instant now = Instant.now();
        JobRepository.JobRecord job = new JobRepository.JobRecord(Ids.newId("job"), jobType, document.sourceId(), documentId, "SUCCEEDED", 100, null, jobType + " completed", "system", 0, 0, 0, 0, null, null, null, now, now, now, now);
        jobRepository.insert(job);
        log.info("Created simple document job jobId={} documentId={} sourceId={} jobType={}", job.id(), documentId, document.sourceId(), jobType);
        return new DocumentController.JobCreationResponse(job.id(), documentId, jobType, job.status());
    }

    /**
     * Retrieves statistics for a single document.
     * @param documentId the document identifier
     * @return the document statistics response
     * @throws IllegalArgumentException if the document is not found
     */
    public DocumentController.DocumentStatsResponse documentStats(String documentId) {
        DocumentRepository.DocumentRecord document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        Instant lastIndexed = jobRepository.findAll().stream()
            .filter(j -> documentId.equals(j.documentId()) && "SUCCEEDED".equals(j.status()))
            .map(JobRepository.JobRecord::updatedAt)
            .max(Comparator.naturalOrder())
            .orElse(document.updatedAt());
        return new DocumentController.DocumentStatsResponse(
            documentId, document.chunkCount(), document.userEditedChunkCount(), lastIndexed, document.status(), document.indexStatus()
        );
    }

    /**
     * Lists chunks with optional source or document filtering and pagination.
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @param sourceId optional source identifier to filter by
     * @param documentId optional document identifier to filter by
     * @return a paginated response of chunk summaries
     */
    public PageResponse<ChunkController.ChunkSummary> listChunks(int page, int pageSize, String sourceId, String documentId) {
        List<ChunkRepository.ChunkRecord> chunks;
        if (documentId != null) {
            chunks = chunkRepository.findByDocumentId(documentId);
        } else if (sourceId != null) {
            chunks = chunkRepository.findBySourceId(sourceId);
        } else {
            chunks = chunkRepository.findAll();
        }
        List<ChunkController.ChunkSummary> items = chunks.stream().map(this::toChunkSummary).toList();
        return page(items, page, pageSize);
    }

    /**
     * Retrieves a single chunk by its identifier.
     * @param chunkId the chunk identifier
     * @return the chunk detail
     * @throws IllegalArgumentException if the chunk is not found
     */
    public ChunkController.ChunkDetail getChunk(String chunkId) {
        return chunkRepository.findById(chunkId).map(this::toChunkDetail)
            .orElseThrow(() -> new IllegalArgumentException("Chunk not found: " + chunkId));
    }

    /**
     * Creates a new chunk for a document and indexes it.
     * @param documentId the document identifier
     * @param request the create chunk request
     * @return the chunk mutation response
     * @throws IllegalArgumentException if the document is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public ChunkController.ChunkMutationResponse createChunk(String documentId, ChunkController.CreateChunkRequest request) {
        DocumentRepository.DocumentRecord document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        ensureSourceWritable(document.sourceId());
        ChunkRepository.ChunkRecord record = new ChunkRepository.ChunkRecord(
            Ids.newId("chk"), documentId, document.sourceId(), request.ordinal(), request.title(), request.titlePath(),
            request.keywords(), request.text(), request.markdown(), request.pageFrom(), request.pageTo(),
            com.huawei.opsfactory.knowledge.common.util.TokenEstimator.estimate(request.text()),
            request.text() == null ? 0 : request.text().length(),
            hash(request.text() + request.markdown()), "USER_EDITED", "system", Instant.now(), Instant.now()
        );
        chunkRepository.insert(record);
        SearchService.SearchableChunk searchableChunk = toSearchableChunk(record);
        Map<String, List<Double>> vectors = embeddingService.ensureChunkEmbeddings(List.of(searchableChunk));
        lexicalIndexService.upsertChunks(List.of(searchableChunk));
        vectorIndexService.upsertChunks(List.of(searchableChunk), vectors);
        refreshDocumentChunkStats(documentId);
        return new ChunkController.ChunkMutationResponse(record.id(), documentId, true, true, record.editStatus(), record.updatedAt());
    }

    /**
     * Updates an existing chunk's content and re-indexes it.
     * @param chunkId the chunk identifier
     * @param request the update chunk request
     * @return the chunk mutation response
     * @throws IllegalArgumentException if the chunk is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public ChunkController.ChunkMutationResponse updateChunk(String chunkId, ChunkController.UpdateChunkRequest request) {
        ChunkRepository.ChunkRecord existing = chunkRepository.findById(chunkId)
            .orElseThrow(() -> new IllegalArgumentException("Chunk not found: " + chunkId));
        ensureSourceWritable(existing.sourceId());
        String text = request.text() != null ? request.text() : existing.text();
        String markdown = request.markdown() != null ? request.markdown() : existing.markdown();
        ChunkRepository.ChunkRecord updated = new ChunkRepository.ChunkRecord(
            existing.id(), existing.documentId(), existing.sourceId(), existing.ordinal(),
            request.title() != null ? request.title() : existing.title(),
            request.titlePath() != null ? request.titlePath() : existing.titlePath(),
            request.keywords() != null ? request.keywords() : existing.keywords(),
            text,
            markdown,
            request.pageFrom() != null ? request.pageFrom() : existing.pageFrom(),
            request.pageTo() != null ? request.pageTo() : existing.pageTo(),
            com.huawei.opsfactory.knowledge.common.util.TokenEstimator.estimate(text),
            text == null ? 0 : text.length(),
            hash(text + markdown),
            "USER_EDITED",
            "system",
            existing.createdAt(),
            Instant.now()
        );
        chunkRepository.update(updated);
        SearchService.SearchableChunk searchableChunk = toSearchableChunk(updated);
        Map<String, List<Double>> vectors = embeddingService.ensureChunkEmbeddings(List.of(searchableChunk));
        lexicalIndexService.upsertChunks(List.of(searchableChunk));
        vectorIndexService.upsertChunks(List.of(searchableChunk), vectors);
        refreshDocumentChunkStats(existing.documentId());
        return new ChunkController.ChunkMutationResponse(chunkId, existing.documentId(), true, true, updated.editStatus(), updated.updatedAt());
    }

    /**
     * Updates the keywords of a chunk and re-indexes it.
     * @param chunkId the chunk identifier
     * @param keywords the new keywords list
     * @return the chunk keywords response
     * @throws IllegalArgumentException if the chunk is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public ChunkController.ChunkKeywordsResponse updateChunkKeywords(String chunkId, List<String> keywords) {
        ChunkRepository.ChunkRecord existing = chunkRepository.findById(chunkId)
            .orElseThrow(() -> new IllegalArgumentException("Chunk not found: " + chunkId));
        ensureSourceWritable(existing.sourceId());
        ChunkRepository.ChunkRecord updated = new ChunkRepository.ChunkRecord(
            existing.id(), existing.documentId(), existing.sourceId(), existing.ordinal(), existing.title(),
            existing.titlePath(), keywords, existing.text(), existing.markdown(), existing.pageFrom(), existing.pageTo(),
            existing.tokenCount(), existing.textLength(), hash(existing.text() + existing.markdown() + keywords),
            "USER_EDITED", "system", existing.createdAt(), Instant.now()
        );
        chunkRepository.update(updated);
        SearchService.SearchableChunk searchableChunk = toSearchableChunk(updated);
        Map<String, List<Double>> vectors = embeddingService.ensureChunkEmbeddings(List.of(searchableChunk));
        lexicalIndexService.upsertChunks(List.of(searchableChunk));
        vectorIndexService.upsertChunks(List.of(searchableChunk), vectors);
        refreshDocumentChunkStats(existing.documentId());
        return new ChunkController.ChunkKeywordsResponse(chunkId, keywords, true, true, updated.updatedAt());
    }

    /**
     * Deletes a chunk and removes it from indexes.
     * @param chunkId the chunk identifier
     * @return the deletion response
     * @throws IllegalArgumentException if the chunk is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public ChunkController.DeleteChunkResponse deleteChunk(String chunkId) {
        ChunkRepository.ChunkRecord existing = chunkRepository.findById(chunkId)
            .orElseThrow(() -> new IllegalArgumentException("Chunk not found: " + chunkId));
        ensureSourceWritable(existing.sourceId());
        lexicalIndexService.deleteChunk(existing.sourceId(), chunkId);
        vectorIndexService.deleteChunk(chunkId);
        chunkRepository.delete(chunkId);
        refreshDocumentChunkStats(existing.documentId());
        return new ChunkController.DeleteChunkResponse(chunkId, true);
    }

    /**
     * Reorders chunks within a document by updating their ordinals.
     * @param documentId the document identifier
     * @param items the reorder items containing chunk id and new ordinal
     * @return the reorder response
     * @throws IllegalArgumentException if the document or a chunk is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public ChunkController.ReorderChunksResponse reorderChunks(String documentId, List<ChunkController.ReorderItem> items) {
        DocumentRepository.DocumentRecord document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        ensureSourceWritable(document.sourceId());
        for (ChunkController.ReorderItem item : items) {
            ChunkRepository.ChunkRecord existing = chunkRepository.findById(item.chunkId())
                .orElseThrow(() -> new IllegalArgumentException("Chunk not found: " + item.chunkId()));
            chunkRepository.update(new ChunkRepository.ChunkRecord(
                existing.id(), existing.documentId(), existing.sourceId(), item.ordinal(), existing.title(), existing.titlePath(),
                existing.keywords(), existing.text(), existing.markdown(), existing.pageFrom(), existing.pageTo(),
                existing.tokenCount(), existing.textLength(), existing.contentHash(), "USER_EDITED", "system", existing.createdAt(), Instant.now()
            ));
        }
        refreshDocumentChunkStats(documentId);
        return new ChunkController.ReorderChunksResponse(documentId, true, true, items.size());
    }

    /**
     * Re-indexes a single chunk, recalculating embeddings and updating indexes.
     * @param chunkId the chunk identifier
     * @return the reindex response
     * @throws IllegalArgumentException if the chunk is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    public ChunkController.ChunkReindexResponse reindexChunk(String chunkId) {
        ChunkRepository.ChunkRecord chunk = chunkRepository.findById(chunkId)
            .orElseThrow(() -> new IllegalArgumentException("Chunk not found: " + chunkId));
        ensureSourceWritable(chunk.sourceId());
        SearchService.SearchableChunk searchableChunk = toSearchableChunk(chunk);
        Map<String, List<Double>> vectors = embeddingService.ensureChunkEmbeddings(List.of(searchableChunk));
        lexicalIndexService.upsertChunks(List.of(searchableChunk));
        vectorIndexService.upsertChunks(List.of(searchableChunk), vectors);
        return new ChunkController.ChunkReindexResponse(chunkId, true, Instant.now());
    }

    /**
     * Performs a search across knowledge sources using the configured retrieval profile.
     * @param request the search request containing query, source ids, and optional overrides
     * @return the search response with ranked hits
     * @throws IllegalArgumentException if a referenced source is not found
     * @throws ApiConflictException if a referenced source is in maintenance or error state
     */
    public RetrievalController.SearchResponse search(RetrievalController.SearchRequest request) {
        long startedAt = System.currentTimeMillis();
        ensureSourcesReadable(resolveReferencedSourceIds(request.sourceIds(), request.documentIds()));
        String retrievalProfileId = resolveSearchRetrievalProfileId(request.retrievalProfileId(), request.sourceIds());
        ResolvedRetrievalSettings settings = resolveRetrievalSettings(retrievalProfileId, request.topK(), request.override());
        List<SearchService.SearchableChunk> searchableChunks = filterChunks(request.sourceIds(), request.documentIds(), request.filters());
        List<SearchService.SearchMatch> matches = searchService.search(searchableChunks, request.query(), settings.toSearchOptions());
        List<RetrievalController.SearchHit> hits = toSearchHits(matches, settings.snippetLength());
        log.info(
            "Search completed query={} mode={} sourceCount={} documentCount={} candidateChunks={} hits={} durationMs={}",
            describeQuery(request.query()),
            settings.mode(),
            countItems(request.sourceIds()),
            countItems(request.documentIds()),
            searchableChunks.size(),
            hits.size(),
            System.currentTimeMillis() - startedAt
        );
        return new RetrievalController.SearchResponse(request.query(), hits, hits.size());
    }

    /**
     * Compares search results across multiple retrieval modes for the same query.
     * @param request the compare search request containing query, sources, and modes to compare
     * @return the compare search response with results per mode
     * @throws IllegalArgumentException if a referenced source is not found
     * @throws ApiConflictException if a referenced source is in maintenance or error state
     */
    public RetrievalController.CompareSearchResponse compare(RetrievalController.CompareSearchRequest request) {
        long startedAt = System.currentTimeMillis();
        ensureSourcesReadable(resolveReferencedSourceIds(request.sourceIds(), request.documentIds()));
        String retrievalProfileId = resolveSearchRetrievalProfileId(request.retrievalProfileId(), request.sourceIds());
        ResolvedRetrievalSettings baseSettings = resolveRetrievalSettings(retrievalProfileId, COMPARE_FETCH_TOP_K, null);
        List<SearchService.SearchableChunk> searchableChunks = filterChunks(request.sourceIds(), request.documentIds(), request.filters());
        List<String> modes = normalizeCompareModes(request.modes());

        RetrievalController.CompareModeResponse hybrid = modes.contains("hybrid")
            ? compareModeResponse(searchableChunks, request.query(), baseSettings.withMode("hybrid", COMPARE_FETCH_TOP_K, null))
            : emptyCompareModeResponse();
        RetrievalController.CompareModeResponse semantic = modes.contains("semantic")
            ? compareModeResponse(searchableChunks, request.query(), baseSettings.withMode("semantic", COMPARE_FETCH_TOP_K, null))
            : emptyCompareModeResponse();
        RetrievalController.CompareModeResponse lexical = modes.contains("lexical")
            ? compareModeResponse(searchableChunks, request.query(), baseSettings.withMode("lexical", COMPARE_FETCH_TOP_K, null))
            : emptyCompareModeResponse();

        RetrievalController.CompareSearchResponse response = new RetrievalController.CompareSearchResponse(
            request.query(),
            COMPARE_FETCH_TOP_K,
            hybrid,
            semantic,
            lexical
        );
        log.info(
            "Compare search completed query={} modes={} sourceCount={} documentCount={} durationMs={}",
            describeQuery(request.query()),
            modes,
            countItems(request.sourceIds()),
            countItems(request.documentIds()),
            System.currentTimeMillis() - startedAt
        );
        return response;
    }

    /**
     * Fetches a chunk by id with optional neighbor context.
     * @param chunkId the chunk identifier
     * @param includeNeighbors whether to include adjacent chunks
     * @param neighborWindow the number of adjacent chunks to include on each side
     * @return the fetch response with chunk content and optional neighbors
     * @throws IllegalArgumentException if the chunk is not found
     * @throws IllegalStateException if neighborWindow is out of range
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    public RetrievalController.FetchResponse fetch(String chunkId, boolean includeNeighbors, int neighborWindow) {
        if (neighborWindow <= 0 || neighborWindow > profileBootstrapService.properties().getFetch().getMaxNeighborWindow()) {
            throw new IllegalStateException("Invalid neighborWindow: " + neighborWindow);
        }
        ChunkRepository.ChunkRecord chunk = chunkRepository.findById(chunkId)
            .orElseThrow(() -> new IllegalArgumentException("Chunk not found: " + chunkId));
        ensureSourceReadable(chunk.sourceId());
        List<RetrievalController.NeighborChunk> neighbors = null;
        if (includeNeighbors) {
            List<ChunkRepository.ChunkRecord> siblings = chunkRepository.findByDocumentId(chunk.documentId());
            neighbors = siblings.stream()
                .filter(s -> Math.abs(s.ordinal() - chunk.ordinal()) <= neighborWindow && !s.id().equals(chunk.id()))
                .map(s -> new RetrievalController.NeighborChunk(s.ordinal() < chunk.ordinal() ? "previous" : "next", s.id(), s.text()))
                .toList();
        }
        List<ChunkRepository.ChunkRecord> siblings = chunkRepository.findByDocumentId(chunk.documentId());
        String previous = siblings.stream().filter(s -> s.ordinal() == chunk.ordinal() - 1).map(ChunkRepository.ChunkRecord::id).findFirst().orElse(null);
        String next = siblings.stream().filter(s -> s.ordinal() == chunk.ordinal() + 1).map(ChunkRepository.ChunkRecord::id).findFirst().orElse(null);
        return new RetrievalController.FetchResponse(
            chunk.id(), chunk.documentId(), chunk.sourceId(), chunk.title(), chunk.titlePath(), chunk.text(), chunk.markdown(),
            chunk.keywords(), chunk.pageFrom(), chunk.pageTo(), previous, next, neighbors
        );
    }

    /**
     * Retrieves evidence chunks for a query across specified sources.
     * @param request the retrieve request containing query and source ids
     * @return the retrieve response with evidence list
     * @throws IllegalArgumentException if a referenced source is not found
     * @throws ApiConflictException if a referenced source is in maintenance or error state
     */
    public RetrievalController.RetrieveResponse retrieve(RetrievalController.RetrieveRequest request) {
        long startedAt = System.currentTimeMillis();
        ensureSourcesReadable(resolveReferencedSourceIds(request.sourceIds(), List.of()));
        RetrievalController.SearchResponse searchResponse = search(new RetrievalController.SearchRequest(
            request.query(), request.sourceIds(), List.of(), request.retrievalProfileId(), request.topK(), null, null
        ));
        List<RetrievalController.Evidence> evidences = searchResponse.hits().stream().map(hit -> {
            RetrievalController.FetchResponse fetched = fetch(hit.chunkId(), false, 1);
            return new RetrievalController.Evidence(
                fetched.chunkId(), fetched.documentId(), fetched.sourceId(), fetched.title(), fetched.text(), fetched.markdown(),
                hit.score(), fetched.keywords(), List.of(new RetrievalController.Reference("page", fetched.pageFrom(), fetched.pageTo()))
            );
        }).toList();
        log.info(
            "Retrieve completed query={} sourceCount={} evidences={} durationMs={}",
            describeQuery(request.query()),
            countItems(request.sourceIds()),
            evidences.size(),
            System.currentTimeMillis() - startedAt
        );
        return new RetrievalController.RetrieveResponse(request.query(), evidences);
    }

    /**
     * Explains the scoring breakdown for a chunk against a query.
     * @param request the explain request containing chunk id and query
     * @return the explain response with lexical, semantic, and fusion scores
     * @throws IllegalArgumentException if the chunk is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    public RetrievalController.ExplainResponse explain(RetrievalController.ExplainRequest request) {
        long startedAt = System.currentTimeMillis();
        ChunkRepository.ChunkRecord chunk = chunkRepository.findById(request.chunkId())
            .orElseThrow(() -> new IllegalArgumentException("Chunk not found: " + request.chunkId()));
        ensureSourceReadable(chunk.sourceId());
        String retrievalProfileId = resolveSearchRetrievalProfileId(request.retrievalProfileId(), List.of(chunk.sourceId()));
        ResolvedRetrievalSettings settings = resolveRetrievalSettings(retrievalProfileId, null, null);
        SearchService.ExplainResult explain = searchService.explain(toSearchableChunk(chunk), request.query(), settings.toSearchOptions());
        log.info(
            "Explain completed chunkId={} sourceId={} query={} durationMs={}",
            request.chunkId(),
            chunk.sourceId(),
            describeQuery(request.query()),
            System.currentTimeMillis() - startedAt
        );
        return new RetrievalController.ExplainResponse(
            request.query(),
            request.chunkId(),
            new RetrievalController.LexicalExplain(explain.matchedFields(), explain.lexicalScore(), 1),
            new RetrievalController.SemanticExplain(explain.semanticScore(), 1),
            new RetrievalController.FusionExplain(explain.fusionMode(), explain.fusionScore())
        );
    }

    /**
     * Executes a search for a single compare mode.
     * @param searchableChunks the candidate chunks to search within
     * @param query the search query
     * @param settings the retrieval settings for this mode
     * @return the compare mode response with hits
     */
    private RetrievalController.CompareModeResponse compareModeResponse(
        List<SearchService.SearchableChunk> searchableChunks,
        String query,
        ResolvedRetrievalSettings settings
    ) {
        List<SearchService.SearchMatch> matches = searchService.search(searchableChunks, query, settings.toSearchOptions());
        List<RetrievalController.SearchHit> hits = toSearchHits(matches, settings.snippetLength());
        return new RetrievalController.CompareModeResponse(hits, hits.size());
    }

    /**
     * Returns an empty compare mode response.
     * @return an empty compare mode response with zero hits
     */
    private RetrievalController.CompareModeResponse emptyCompareModeResponse() {
        return new RetrievalController.CompareModeResponse(List.of(), 0);
    }

    /**
     * Converts search matches to search hits with truncated snippets.
     * @param matches the search matches
     * @param snippetLength the maximum snippet length
     * @return the list of search hits
     */
    private List<RetrievalController.SearchHit> toSearchHits(List<SearchService.SearchMatch> matches, int snippetLength) {
        return matches.stream().map(match -> {
            SearchService.SearchableChunk chunk = match.chunk();
            String text = chunk.text() == null ? "" : chunk.text();
            String snippet = text.length() > snippetLength ? text.substring(0, snippetLength) : text;
            return new RetrievalController.SearchHit(
                chunk.id(), chunk.documentId(), chunk.sourceId(), chunk.title(), chunk.titlePath(), snippet,
                match.score(), match.lexicalScore(), match.semanticScore(), match.fusionScore(), chunk.pageFrom(), chunk.pageTo()
            );
        }).toList();
    }

    /**
     * Lists all index profiles with pagination.
     *
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return a paginated response of index profile summaries
     */
    /**
     * Lists all index profiles with pagination.
     *
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return a paginated response of index profile summaries
     */
    public PageResponse<ProfileController.ProfileSummary> listIndexProfiles(int page, int pageSize) {
        List<ProfileController.ProfileSummary> items = profileRepository.findAllIndex().stream()
            .map(this::toProfileSummary)
            .toList();
        return page(items, page, pageSize);
    }

    /**
     * Lists all retrieval profiles with pagination.
     *
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return a paginated response of retrieval profile summaries
     */
    /**
     * Lists all retrieval profiles with pagination.
     *
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return a paginated response of retrieval profile summaries
     */
    public PageResponse<ProfileController.ProfileSummary> listRetrievalProfiles(int page, int pageSize) {
        List<ProfileController.ProfileSummary> items = profileRepository.findAllRetrieval().stream()
            .map(this::toProfileSummary)
            .toList();
        return page(items, page, pageSize);
    }

    /**
     * Creates a new index profile.
     *
     * @param request the create profile request
     * @return the created profile detail
     * @throws IllegalArgumentException if the profile name is invalid
     * @throws IllegalStateException if the profile config is invalid
     * @throws ApiConflictException if the profile name already exists
     */
    /**
     * Creates a new index profile.
     *
     * @param request the create profile request
     * @return the created profile detail
     * @throws IllegalArgumentException if the profile name is invalid
     * @throws IllegalStateException if the profile config is invalid
     * @throws ApiConflictException if the profile name already exists
     */
    @Transactional
    public ProfileController.ProfileDetail createIndexProfile(ProfileController.CreateProfileRequest request) {
        validateIndexProfileConfig(request.config());
        Instant now = Instant.now();
        String profileName = request.name() != null ? request.name().trim() : null;
        ensureProfileNameAvailable(profileName, null, true);
        ProfileRepository.ProfileRecord record = new ProfileRepository.ProfileRecord(
            Ids.newId("ip"),
            profileName,
            request.config(),
            "index",
            null,
            false,
            null,
            now,
            now
        );
        profileRepository.insertIndex(record);
        return toProfileDetail(record);
    }

    /**
     * Creates a new retrieval profile.
     *
     * @param request the create profile request
     * @return the created profile detail
     * @throws IllegalArgumentException if the profile name is invalid
     * @throws ApiConflictException if the profile name already exists
     */
    /**
     * Creates a new retrieval profile.
     *
     * @param request the create profile request
     * @return the created profile detail
     * @throws IllegalArgumentException if the profile name is invalid
     * @throws ApiConflictException if the profile name already exists
     */
    @Transactional
    public ProfileController.ProfileDetail createRetrievalProfile(ProfileController.CreateProfileRequest request) {
        Instant now = Instant.now();
        String profileName = request.name() != null ? request.name().trim() : null;
        ensureProfileNameAvailable(profileName, null, false);
        ProfileRepository.ProfileRecord record = new ProfileRepository.ProfileRecord(
            Ids.newId("rp"),
            profileName,
            request.config(),
            "retrieval",
            null,
            false,
            null,
            now,
            now
        );
        profileRepository.insertRetrieval(record);
        return toProfileDetail(record);
    }

    /**
     * Retrieves an index profile by its identifier.
     *
     * @param id the profile identifier
     * @return the profile detail
     * @throws IllegalArgumentException if the profile is not found
     */
    /**
     * Retrieves an index profile by its identifier.
     *
     * @param id the profile identifier
     * @return the profile detail
     * @throws IllegalArgumentException if the profile is not found
     */
    public ProfileController.ProfileDetail getIndexProfile(String id) {
        ProfileRepository.ProfileRecord record = profileRepository.findIndexById(id).orElseThrow(() -> new IllegalArgumentException("Index profile not found: " + id));
        return toProfileDetail(record);
    }

    /**
     * Retrieves a retrieval profile by its identifier.
     *
     * @param id the profile identifier
     * @return the profile detail
     * @throws IllegalArgumentException if the profile is not found
     */
    /**
     * Retrieves a retrieval profile by its identifier.
     *
     * @param id the profile identifier
     * @return the profile detail
     * @throws IllegalArgumentException if the profile is not found
     */
    public ProfileController.ProfileDetail getRetrievalProfile(String id) {
        ProfileRepository.ProfileRecord record = profileRepository.findRetrievalById(id).orElseThrow(() -> new IllegalArgumentException("Retrieval profile not found: " + id));
        return toProfileDetail(record);
    }

    /**
     * Updates an existing index profile.
     *
     * @param id the profile identifier
     * @param request the update profile request
     * @return the update response
     * @throws IllegalArgumentException if the profile is not found
     * @throws ApiConflictException if the profile is read-only or name already exists
     * @throws IllegalStateException if the profile config is invalid
     */
    /**
     * Updates an existing index profile.
     *
     * @param id the profile identifier
     * @param request the update profile request
     * @return the update response
     * @throws IllegalArgumentException if the profile is not found
     * @throws IllegalStateException if the profile config is invalid
     * @throws ApiConflictException if the profile is read-only or name already exists
     */
    @Transactional
    public ProfileController.ProfileUpdateResponse updateIndexProfile(String id, ProfileController.UpdateProfileRequest request) {
        ProfileRepository.ProfileRecord existing = profileRepository.findIndexById(id).orElseThrow(() -> new IllegalArgumentException("Index profile not found: " + id));
        ensureProfileMutable(existing, "index");
        validateIndexProfileConfig(request.config());
        String profileName = request.name() != null ? request.name().trim() : existing.name();
        ensureProfileNameAvailable(profileName, id, true);
        ProfileRepository.ProfileRecord updated = new ProfileRepository.ProfileRecord(
            id,
            profileName,
            mergeMaps(existing.config(), request.config()),
            "index",
            existing.ownerSourceId(),
            existing.readonly(),
            existing.derivedFromProfileId(),
            existing.createdAt(),
            Instant.now()
        );
        profileRepository.updateIndex(updated);
        if (request.config() != null && !request.config().isEmpty()) {
            markSourcesRebuildRequiredByIndexProfile(id);
        }
        return new ProfileController.ProfileUpdateResponse(id, updated.name(), updated.updatedAt());
    }

    /**
     * Updates an existing retrieval profile.
     *
     * @param id the profile identifier
     * @param request the update profile request
     * @return the update response
     * @throws IllegalArgumentException if the profile is not found
     * @throws ApiConflictException if the profile is read-only or name already exists
     */
    @Transactional
    public ProfileController.ProfileUpdateResponse updateRetrievalProfile(String id, ProfileController.UpdateProfileRequest request) {
        ProfileRepository.ProfileRecord existing = profileRepository.findRetrievalById(id).orElseThrow(() -> new IllegalArgumentException("Retrieval profile not found: " + id));
        ensureProfileMutable(existing, "retrieval");
        String profileName = request.name() != null ? request.name().trim() : existing.name();
        ensureProfileNameAvailable(profileName, id, false);
        ProfileRepository.ProfileRecord updated = new ProfileRepository.ProfileRecord(
            id,
            profileName,
            mergeMaps(existing.config(), request.config()),
            "retrieval",
            existing.ownerSourceId(),
            existing.readonly(),
            existing.derivedFromProfileId(),
            existing.createdAt(),
            Instant.now()
        );
        profileRepository.updateRetrieval(updated);
        return new ProfileController.ProfileUpdateResponse(id, updated.name(), updated.updatedAt());
    }

    /**
     * Deletes an index profile by its identifier.
     *
     * @param id the profile identifier
     * @return the deletion response
     * @throws IllegalArgumentException if the profile is not found
     * @throws IllegalStateException if the profile is still bound to a source
     * @throws ApiConflictException if the profile is read-only
     */
    @Transactional
    public ProfileController.DeleteProfileResponse deleteIndexProfile(String id) {
        ensureProfileMutable(
            profileRepository.findIndexById(id).orElseThrow(() -> new IllegalArgumentException("Index profile not found: " + id)),
            "index"
        );
        ensureProfileNotBound(id, true);
        profileRepository.deleteIndex(id);
        return new ProfileController.DeleteProfileResponse(id, true);
    }

    /**
     * Deletes a retrieval profile by its identifier.
     *
     * @param id the profile identifier
     * @return the deletion response
     * @throws IllegalArgumentException if the profile is not found
     * @throws IllegalStateException if the profile is still bound to a source
     * @throws ApiConflictException if the profile is read-only
     */
    @Transactional
    public ProfileController.DeleteProfileResponse deleteRetrievalProfile(String id) {
        ensureProfileMutable(
            profileRepository.findRetrievalById(id).orElseThrow(() -> new IllegalArgumentException("Retrieval profile not found: " + id)),
            "retrieval"
        );
        ensureProfileNotBound(id, false);
        profileRepository.deleteRetrieval(id);
        return new ProfileController.DeleteProfileResponse(id, true);
    }

    /**
     * Lists all profile bindings with pagination.
     *
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return a paginated response of profile bindings
     */
    public PageResponse<ProfileController.BindingResponse> listBindings(int page, int pageSize) {
        List<ProfileController.BindingResponse> items = bindingRepository.findAll().stream()
            .map(b -> new ProfileController.BindingResponse(b.sourceId(), b.indexProfileId(), b.retrievalProfileId(), b.updatedAt()))
            .toList();
        return page(items, page, pageSize);
    }

    /**
     * Binds index and retrieval profiles to a knowledge source.
     *
     * @param request the binding request containing source id and profile ids
     * @return the binding response
     * @throws IllegalArgumentException if the source or profiles are not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public ProfileController.BindingResponse bindProfiles(ProfileController.BindingRequest request) {
        SourceRepository.SourceRecord source = sourceRepository.findById(request.sourceId())
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + request.sourceId()));
        ensureSourceWritable(source);
        validateIndexProfileBindableToSource(request.indexProfileId(), request.sourceId());
        validateRetrievalProfileBindableToSource(request.retrievalProfileId(), request.sourceId());
        Instant now = Instant.now();
        bindingRepository.upsert(new BindingRepository.BindingRecord(Ids.newId("spb"), request.sourceId(), request.indexProfileId(), request.retrievalProfileId(), now, now));
        sourceRepository.update(new SourceRepository.SourceRecord(
            source.id(), source.name(), source.description(), source.status(), source.storageMode(),
            request.indexProfileId(), request.retrievalProfileId(), source.runtimeStatus(), source.runtimeMessage(),
            source.currentJobId(), source.lastJobError(), source.rebuildRequired() || !request.indexProfileId().equals(source.indexProfileId()),
            source.createdAt(), now
        ));
        return new ProfileController.BindingResponse(request.sourceId(), request.indexProfileId(), request.retrievalProfileId(), now);
    }

    /**
     * Triggers a full rebuild of a knowledge source, re-parsing and re-indexing all documents.
     *
     * @param sourceId the source identifier
     * @return the rebuild response containing job id and status
     * @throws IllegalArgumentException if the source is not found
     * @throws ApiConflictException if the source is already in maintenance
     */
    @Transactional
    public SourceController.RebuildSourceResponse rebuildSource(String sourceId) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        if ("MAINTENANCE".equals(source.runtimeStatus())) {
            throw new ApiConflictException("REBUILD_ALREADY_RUNNING", "当前知识库正在重建，请稍后再试");
        }
        Instant now = Instant.now();
        JobRepository.JobRecord job = new JobRepository.JobRecord(
            Ids.newId("job"), "SOURCE_REBUILD", sourceId, null, "RUNNING", 0, "PREPARING", "Source rebuild started",
            "admin", 0, 0, 0, 0, null, null, null, now, null, now, now
        );
        jobRepository.insert(job);
        sourceRepository.update(withRuntimeState(source, "MAINTENANCE", "知识库重建中，请稍后再试", job.id(), source.lastJobError(), source.rebuildRequired(), now));
        log.info("Queued source rebuild sourceId={} jobId={}", sourceId, job.id());
        taskExecutor.execute(() -> runSourceRebuild(job.id(), sourceId));
        return new SourceController.RebuildSourceResponse(job.id(), sourceId, "RUNNING");
    }

    /**
     * Retrieves the maintenance overview for a knowledge source, including current and last completed rebuild jobs.
     *
     * @param sourceId the source identifier
     * @return the maintenance overview response
     * @throws IllegalArgumentException if the source is not found
     */
    public SourceController.MaintenanceOverviewResponse maintenanceOverview(String sourceId) {
        sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        JobRepository.JobRecord currentJob = jobRepository.findAll().stream()
            .filter(job -> sourceId.equals(job.sourceId()) && "SOURCE_REBUILD".equals(job.jobType()) && "RUNNING".equals(job.status()))
            .findFirst()
            .orElse(null);
        JobRepository.JobRecord lastCompletedJob = jobRepository.findAll().stream()
            .filter(job -> sourceId.equals(job.sourceId()) && "SOURCE_REBUILD".equals(job.jobType()) && job.finishedAt() != null)
            .max(Comparator.comparing(JobRepository.JobRecord::finishedAt))
            .orElse(null);
        return new SourceController.MaintenanceOverviewResponse(
            sourceId,
            toMaintenanceJobSummary(currentJob),
            toMaintenanceJobSummary(lastCompletedJob)
        );
    }

    /**
     * Partially updates the profile binding for a knowledge source.
     *
     * @param sourceId the source identifier
     * @param request the patch request containing optional index and retrieval profile ids
     * @return the updated binding response
     * @throws IllegalArgumentException if the binding or source is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    public ProfileController.BindingResponse updateBinding(String sourceId, ProfileController.BindingPatchRequest request) {
        BindingRepository.BindingRecord existingBinding = bindingRepository.findBySourceId(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Binding not found for source: " + sourceId));
        return bindProfiles(new ProfileController.BindingRequest(
            sourceId,
            request.indexProfileId() != null ? request.indexProfileId() : existingBinding.indexProfileId(),
            request.retrievalProfileId() != null ? request.retrievalProfileId() : existingBinding.retrievalProfileId()
        ));
    }

    /**
     * Retrieves the index profile configuration for a knowledge source.
     *
     * @param sourceId the source identifier
     * @return the source index profile configuration response
     * @throws IllegalArgumentException if the source or index profile is not found
     */
    public SourceController.SourceProfileConfigResponse getSourceIndexProfileConfig(String sourceId) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ProfileRepository.ProfileRecord profile = profileRepository.findIndexById(source.indexProfileId())
            .orElseThrow(() -> new IllegalArgumentException("Index profile not found: " + source.indexProfileId()));
        return toSourceProfileConfigResponse(source, profile, false);
    }

    /**
     * Updates the index profile configuration for a knowledge source, forking from default if necessary.
     *
     * @param sourceId the source identifier
     * @param request the update request containing optional name and config overrides
     * @return the updated source index profile configuration response
     * @throws IllegalArgumentException if the source or index profile is not found
     * @throws IllegalStateException if the profile config is invalid
     * @throws ApiConflictException if the source is in maintenance or error state, or profile name already exists
     */
    @Transactional
    public SourceController.SourceProfileConfigResponse putSourceIndexProfileConfig(
        String sourceId,
        SourceController.UpdateSourceProfileConfigRequest request
    ) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ensureSourceWritable(source);
        validateIndexProfileConfig(request.config());

        ProfileRepository.ProfileRecord current = profileRepository.findIndexById(source.indexProfileId())
            .orElseThrow(() -> new IllegalArgumentException("Index profile not found: " + source.indexProfileId()));
        Instant now = Instant.now();

        boolean createdFromDefault = false;
        ProfileRepository.ProfileRecord target = current;
        if (shouldForkProfileForSource(current, sourceId, profileBootstrapService.defaultIndexProfileId())) {
            createdFromDefault = true;
            String targetName = sourceOwnedProfileName(source, "index", request.name(), current.name(), true);
            ensureProfileNameAvailable(targetName, null, true);
            target = new ProfileRepository.ProfileRecord(
                Ids.newId("ip"),
                targetName,
                current.config(),
                "index",
                sourceId,
                false,
                current.id(),
                now,
                now
            );
            profileRepository.insertIndex(target);
        }

        String updatedName = sourceOwnedProfileName(source, "index", request.name(), target.name(), createdFromDefault);
        ensureProfileNameAvailable(updatedName, target.id(), true);
        ProfileRepository.ProfileRecord updated = new ProfileRepository.ProfileRecord(
            target.id(),
            updatedName,
            mergeMaps(target.config(), request.config()),
            "index",
            sourceId,
            false,
            target.derivedFromProfileId(),
            target.createdAt(),
            now
        );
        profileRepository.updateIndex(updated);

        SourceRepository.SourceRecord updatedSource = withBoundProfiles(
            source,
            updated.id(),
            source.retrievalProfileId(),
            true,
            now
        );
        sourceRepository.update(updatedSource);
        bindingRepository.upsert(new BindingRepository.BindingRecord(
            Ids.newId("spb"), sourceId, updated.id(), updatedSource.retrievalProfileId(), now, now
        ));

        return toSourceProfileConfigResponse(updatedSource, updated, createdFromDefault);
    }

    /**
     * Resets the index profile configuration for a knowledge source to the system default.
     *
     * @param sourceId the source identifier
     * @return the reset source index profile configuration response
     * @throws IllegalArgumentException if the source or index profile is not found
     * @throws IllegalStateException if the system default index profile is unavailable
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public SourceController.SourceProfileConfigResponse resetSourceIndexProfileConfig(String sourceId) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ensureSourceWritable(source);

        String defaultProfileId = profileBootstrapService.defaultIndexProfileId();
        if (defaultProfileId == null) {
            throw new IllegalStateException("System default index profile is unavailable");
        }

        ProfileRepository.ProfileRecord current = profileRepository.findIndexById(source.indexProfileId())
            .orElseThrow(() -> new IllegalArgumentException("Index profile not found: " + source.indexProfileId()));
        ProfileRepository.ProfileRecord defaultProfile = profileRepository.findIndexById(defaultProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Index profile not found: " + defaultProfileId));
        Instant now = Instant.now();

        SourceRepository.SourceRecord updatedSource = withBoundProfiles(
            source,
            defaultProfileId,
            source.retrievalProfileId(),
            true,
            now
        );
        sourceRepository.update(updatedSource);
        bindingRepository.upsert(new BindingRepository.BindingRecord(
            Ids.newId("spb"), sourceId, defaultProfileId, updatedSource.retrievalProfileId(), now, now
        ));
        deleteOwnedProfileIfPresent(current, true, sourceId, defaultProfileId);

        return toSourceProfileConfigResponse(updatedSource, defaultProfile, false);
    }

    /**
     * Retrieves the retrieval profile configuration for a knowledge source.
     *
     * @param sourceId the source identifier
     * @return the source retrieval profile configuration response
     * @throws IllegalArgumentException if the source or retrieval profile is not found
     */
    public SourceController.SourceProfileConfigResponse getSourceRetrievalProfileConfig(String sourceId) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ProfileRepository.ProfileRecord profile = profileRepository.findRetrievalById(source.retrievalProfileId())
            .orElseThrow(() -> new IllegalArgumentException("Retrieval profile not found: " + source.retrievalProfileId()));
        return toSourceProfileConfigResponse(source, profile, false);
    }

    /**
     * Updates the retrieval profile configuration for a knowledge source, forking from default if necessary.
     *
     * @param sourceId the source identifier
     * @param request the update request containing optional name and config overrides
     * @return the updated source retrieval profile configuration response
     * @throws IllegalArgumentException if the source or retrieval profile is not found
     * @throws ApiConflictException if the source is in maintenance or error state, or profile name already exists
     */
    @Transactional
    public SourceController.SourceProfileConfigResponse putSourceRetrievalProfileConfig(
        String sourceId,
        SourceController.UpdateSourceProfileConfigRequest request
    ) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ensureSourceWritable(source);

        ProfileRepository.ProfileRecord current = profileRepository.findRetrievalById(source.retrievalProfileId())
            .orElseThrow(() -> new IllegalArgumentException("Retrieval profile not found: " + source.retrievalProfileId()));
        Instant now = Instant.now();

        boolean createdFromDefault = false;
        ProfileRepository.ProfileRecord target = current;
        if (shouldForkProfileForSource(current, sourceId, profileBootstrapService.defaultRetrievalProfileId())) {
            createdFromDefault = true;
            String targetName = sourceOwnedProfileName(source, "retrieval", request.name(), current.name(), true);
            ensureProfileNameAvailable(targetName, null, false);
            target = new ProfileRepository.ProfileRecord(
                Ids.newId("rp"),
                targetName,
                current.config(),
                "retrieval",
                sourceId,
                false,
                current.id(),
                now,
                now
            );
            profileRepository.insertRetrieval(target);
        }

        String updatedName = sourceOwnedProfileName(source, "retrieval", request.name(), target.name(), createdFromDefault);
        ensureProfileNameAvailable(updatedName, target.id(), false);
        ProfileRepository.ProfileRecord updated = new ProfileRepository.ProfileRecord(
            target.id(),
            updatedName,
            mergeMaps(target.config(), request.config()),
            "retrieval",
            sourceId,
            false,
            target.derivedFromProfileId(),
            target.createdAt(),
            now
        );
        profileRepository.updateRetrieval(updated);

        SourceRepository.SourceRecord updatedSource = withBoundProfiles(
            source,
            source.indexProfileId(),
            updated.id(),
            source.rebuildRequired(),
            now
        );
        sourceRepository.update(updatedSource);
        bindingRepository.upsert(new BindingRepository.BindingRecord(
            Ids.newId("spb"), sourceId, updatedSource.indexProfileId(), updated.id(), now, now
        ));

        return toSourceProfileConfigResponse(updatedSource, updated, createdFromDefault);
    }

    /**
     * Resets a source's retrieval profile configuration to the system default.
     *
     * @param sourceId the source identifier
     * @return the source profile config response with the default retrieval profile
     * @throws IllegalArgumentException if the source or profile is not found
     * @throws IllegalStateException if the system default retrieval profile is unavailable
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    @Transactional
    public SourceController.SourceProfileConfigResponse resetSourceRetrievalProfileConfig(String sourceId) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ensureSourceWritable(source);

        String defaultProfileId = profileBootstrapService.defaultRetrievalProfileId();
        if (defaultProfileId == null) {
            throw new IllegalStateException("System default retrieval profile is unavailable");
        }

        ProfileRepository.ProfileRecord current = profileRepository.findRetrievalById(source.retrievalProfileId())
            .orElseThrow(() -> new IllegalArgumentException("Retrieval profile not found: " + source.retrievalProfileId()));
        ProfileRepository.ProfileRecord defaultProfile = profileRepository.findRetrievalById(defaultProfileId)
            .orElseThrow(() -> new IllegalArgumentException("Retrieval profile not found: " + defaultProfileId));
        Instant now = Instant.now();

        SourceRepository.SourceRecord updatedSource = withBoundProfiles(
            source,
            source.indexProfileId(),
            defaultProfileId,
            source.rebuildRequired(),
            now
        );
        sourceRepository.update(updatedSource);
        bindingRepository.upsert(new BindingRepository.BindingRecord(
            Ids.newId("spb"), sourceId, updatedSource.indexProfileId(), defaultProfileId, now, now
        ));
        deleteOwnedProfileIfPresent(current, false, sourceId, defaultProfileId);

        return toSourceProfileConfigResponse(updatedSource, defaultProfile, false);
    }

    /**
     * Retrieves a single job by its identifier.
     *
     * @param jobId the job identifier
     * @return the job response
     * @throws IllegalArgumentException if the job is not found
     */
    public JobController.JobResponse getJob(String jobId) {
        return jobRepository.findById(jobId).map(this::toJobResponse)
            .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
    }

    /**
     * Lists all jobs with pagination.
     *
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return a paginated response of job summaries
     */
    public PageResponse<JobController.JobResponse> listJobs(int page, int pageSize) {
        List<JobController.JobResponse> items = jobRepository.findAll().stream().map(this::toJobResponse).toList();
        return page(items, page, pageSize);
    }

    /**
     * Cancels a running job by updating its status to cancelled.
     *
     * @param jobId the job identifier
     * @return the job cancel response
     * @throws IllegalArgumentException if the job is not found
     */
    @Transactional
    public JobController.JobCancelResponse cancelJob(String jobId) {
        JobRepository.JobRecord existing = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        JobRepository.JobRecord updated = new JobRepository.JobRecord(existing.id(), existing.jobType(), existing.sourceId(), existing.documentId(), "CANCELLED", existing.progress(), existing.stage(), existing.message(), existing.createdBy(), existing.totalDocuments(), existing.processedDocuments(), existing.successDocuments(), existing.failedDocuments(), existing.currentDocumentId(), existing.currentDocumentName(), existing.errorSummary(), existing.startedAt(), Instant.now(), existing.createdAt(), Instant.now());
        jobRepository.update(updated);
        log.info("Cancelled job jobId={} jobType={} sourceId={} documentId={}", jobId, existing.jobType(), existing.sourceId(), existing.documentId());
        return new JobController.JobCancelResponse(jobId, true, "CANCELLED", updated.updatedAt());
    }

    /**
     * Retries a failed job by creating a new succeeded job record.
     *
     * @param jobId the job identifier of the failed job to retry
     * @return the job retry response with the new job id
     * @throws IllegalArgumentException if the job is not found
     * @throws IllegalStateException if the job is not in failed status
     */
    @Transactional
    public JobController.JobRetryResponse retryJob(String jobId) {
        JobRepository.JobRecord existing = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        if (!"FAILED".equals(existing.status())) {
            throw new IllegalStateException("Only failed jobs can be retried");
        }
        Instant now = Instant.now();
        JobRepository.JobRecord retry = new JobRepository.JobRecord(Ids.newId("job"), existing.jobType(), existing.sourceId(), existing.documentId(), "SUCCEEDED", 100, existing.stage(), "Retry completed", "system", existing.totalDocuments(), existing.processedDocuments(), existing.successDocuments(), existing.failedDocuments(), existing.currentDocumentId(), existing.currentDocumentName(), existing.errorSummary(), now, now, now, now);
        jobRepository.insert(retry);
        log.info("Retried job previousJobId={} retryJobId={} jobType={} sourceId={} documentId={}", jobId, retry.id(), existing.jobType(), existing.sourceId(), existing.documentId());
        return new JobController.JobRetryResponse(retry.id(), jobId, retry.status());
    }

    /**
     * Retrieves the logs for a specific job.
     *
     * @param jobId the job identifier
     * @return the job logs response
     * @throws IllegalArgumentException if the job is not found
     */
    public JobController.JobLogsResponse logs(String jobId) {
        JobController.JobResponse job = getJob(jobId);
        return new JobController.JobLogsResponse(jobId, List.of(new JobController.JobLogEntry(job.updatedAt(), "INFO", job.message())));
    }

    /**
     * Retrieves the failure details for a specific job.
     *
     * @param jobId the job identifier
     * @return the job failures response with a list of failure entries
     * @throws IllegalArgumentException if the job is not found
     */
    public JobController.JobFailuresResponse jobFailures(String jobId) {
        jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        return new JobController.JobFailuresResponse(
            jobId,
            maintenanceJobFailureRepository.findByJobId(jobId).stream()
                .map(failure -> new JobController.JobFailureEntry(
                    failure.documentId(),
                    failure.documentName(),
                    failure.stage(),
                    failure.errorCode(),
                    failure.message(),
                    failure.finishedAt()
                ))
                .toList()
        );
    }

    /**
     * Retrieves the overall statistics for the knowledge service.
     *
     * @return the overview stats response containing source, document, chunk, and job counts
     */
    public com.huawei.opsfactory.knowledge.api.stats.StatsController.OverviewStatsResponse overviewStats() {
        return new com.huawei.opsfactory.knowledge.api.stats.StatsController.OverviewStatsResponse(
            sourceRepository.findAll().size(),
            (int) documentRepository.count(),
            (int) documentRepository.countByStatus("INDEXED"),
            (int) documentRepository.countByStatus("ERROR"),
            (int) documentRepository.countByStatus("PROCESSING"),
            (int) chunkRepository.count(),
            (int) chunkRepository.countUserEdited(),
            (int) jobRepository.countRunning()
        );
    }

    /**
     * Processes a file upload for a source, returning whether it was imported.
     *
     * @param sourceId the source identifier
     * @param file the multipart file to upload
     * @return true if the file was imported, false otherwise
     * @throws IllegalStateException if the upload processing fails
     * @throws RuntimeException if an unexpected error occurs during processing
     */
    private boolean processUpload(String sourceId, MultipartFile file) {
        return processUploadWithResult(sourceId, file).imported();
    }

    /**
     * 文档上传操作的结果。
     *
     * @param imported 是否成功导入，true 表示导入成功，false 表示被跳过
     * @param skipReason 跳过原因（导入成功时为 null），例如："DUPLICATE_CONTENT"
     * @param existingFileName 已存在文档的名称（导入成功或无冲突时为 null）
     */
    private record UploadResult(boolean imported, String skipReason, String existingFileName) {
    }

    /**
     * Processes a file upload for a source, returning a detailed result.
     *
     * @param sourceId the source identifier
     * @param file the multipart file to upload
     * @return the upload result indicating import status, skip reason, and existing file name
     * @throws IllegalStateException if the upload processing fails or content type is unsupported
     * @throws RuntimeException if an unexpected error occurs during processing
     */
    private UploadResult processUploadWithResult(String sourceId, MultipartFile file) {
        try {
            String documentId = Ids.newId("doc");
            putMdcIfText(LoggingKeys.SOURCE_ID, sourceId);
            putMdcIfText(LoggingKeys.DOCUMENT_ID, documentId);
            String sha256 = sha256(file.getInputStream());
            var existingDoc = documentRepository.findBySourceIdAndSha256(sourceId, sha256);
            if (existingDoc.isPresent()) {
                log.info("Skipped duplicate upload sourceId={} documentName={} existingDocumentName={} sha256={}", sourceId, file.getOriginalFilename(), existingDoc.get().name(), sha256);
                return new UploadResult(false, "DUPLICATE_CONTENT", existingDoc.get().name());
            }
            Path originalPath = storageManager.originalFilePath(sourceId, documentId, file.getOriginalFilename());
            storageManager.save(file.getInputStream(), originalPath);
            TikaConversionService.ConversionResult conversion = conversionService.convert(originalPath);
            if (!isAllowedContentType(Optional.ofNullable(file.getContentType()).orElse(conversion.contentType()), conversion.contentType())) {
                storageManager.deleteRecursively(storageManager.uploadDocumentDir(sourceId, documentId));
                log.warn(
                    "Rejected upload sourceId={} documentId={} documentName={} requestContentType={} detectedContentType={}",
                    sourceId,
                    documentId,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    conversion.contentType()
                );
                throw new IllegalStateException("Unsupported content type: " + conversion.contentType());
            }
            String persistedContentType = resolvePersistedContentType(
                file.getContentType(),
                conversion.contentType(),
                file.getOriginalFilename()
            );
            Instant now = Instant.now();
            DocumentRepository.DocumentRecord doc = new DocumentRepository.DocumentRecord(
                documentId, sourceId, file.getOriginalFilename(), file.getOriginalFilename(), conversion.title(), null, List.of(),
                sha256, persistedContentType, "zh",
                "INDEXED", "INDEXED", file.getSize(), 0, 0, null, "system", now, now
            );
            documentRepository.insert(doc);
            Path artifactDir = storageManager.artifactDir(sourceId, documentId);
            storageManager.writeString(artifactDir.resolve("content.md"), conversion.markdown());
            List<ChunkingService.ChunkDraft> chunks = chunkingService.chunk(conversion.title(), conversion.text(), conversion.markdown());
            List<SearchService.SearchableChunk> insertedChunks = new ArrayList<>();
            for (ChunkingService.ChunkDraft draft : chunks) {
                ChunkRepository.ChunkRecord chunkRecord = new ChunkRepository.ChunkRecord(
                    Ids.newId("chk"), documentId, sourceId, draft.ordinal(), draft.title(), draft.titlePath(), draft.keywords(),
                    draft.text(), draft.markdown(), 1, 1, draft.tokenCount(), draft.textLength(), hash(draft.text() + draft.markdown()),
                    "SYSTEM_GENERATED", "system", now, now
                );
                chunkRepository.insert(chunkRecord);
                insertedChunks.add(toSearchableChunk(chunkRecord));
            }
            Map<String, List<Double>> vectors = embeddingService.ensureChunkEmbeddings(insertedChunks);
            lexicalIndexService.upsertChunks(insertedChunks);
            vectorIndexService.upsertChunks(insertedChunks, vectors);
            refreshDocumentChunkStats(documentId);
            log.info(
                "Processed upload sourceId={} documentId={} documentName={} contentType={} chunkCount={} fileSizeBytes={}",
                sourceId,
                documentId,
                file.getOriginalFilename(),
                persistedContentType,
                insertedChunks.size(),
                file.getSize()
            );
            return new UploadResult(true, null, null);
        } catch (Exception e) {
            log.error("Failed to process upload sourceId={} documentName={}", sourceId, file.getOriginalFilename(), e);
            throw new IllegalStateException("Failed to ingest file " + file.getOriginalFilename(), e);
        } finally {
            removeMdcIfText(LoggingKeys.DOCUMENT_ID, null);
            removeMdcIfText(LoggingKeys.SOURCE_ID, sourceId);
        }
    }

    /**
     * Checks whether the given content types are allowed for upload.
     *
     * @param requestContentType the content type from the request
     * @param detectedContentType the content type detected by Tika
     * @return true if either content type is allowed
     */
    private boolean isAllowedContentType(String requestContentType, String detectedContentType) {
        Set<String> allowed = profileBootstrapService.allowedContentTypes().stream()
            .map(KnowledgeServiceFacade::normalizeContentType)
            .collect(Collectors.toSet());
        return allowed.contains(normalizeContentType(requestContentType))
            || allowed.contains(normalizeContentType(detectedContentType));
    }

    /**
     * Resolves the persisted content type from request, detected, and file name hints.
     *
     * @param requestContentType the content type from the upload request
     * @param detectedContentType the content type detected by Tika
     * @param fileName the original file name
     * @return the resolved content type to persist
     */
    static String resolvePersistedContentType(String requestContentType, String detectedContentType, String fileName) {
        String normalizedRequest = normalizeContentType(requestContentType);
        String normalizedDetected = normalizeContentType(detectedContentType);
        boolean chmUpload = isChmFileName(fileName)
            || CHM_CONTENT_TYPES.contains(normalizedRequest)
            || CHM_CONTENT_TYPES.contains(normalizedDetected);

        if (chmUpload && StringUtils.hasText(normalizedDetected)) {
            return normalizedDetected;
        }
        if (!StringUtils.hasText(normalizedRequest) || GENERIC_CONTENT_TYPES.contains(normalizedRequest)) {
            return StringUtils.hasText(normalizedDetected) ? normalizedDetected : normalizedRequest;
        }
        return normalizedRequest;
    }

    /**
     * Normalizes a content type string by trimming, removing parameters, and lower-casing.
     *
     * @param contentType the raw content type string
     * @return the normalized content type
     */
    private static String normalizeContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return "";
        }
        return contentType
            .trim()
            .split(";", 2)[0]
            .trim()
            .toLowerCase(Locale.ROOT);
    }

    /**
     * Checks whether the given file name has a .chm extension.
     *
     * @param fileName the file name to check
     * @return true if the file name ends with .chm
     */
    private static boolean isChmFileName(String fileName) {
        return StringUtils.hasText(fileName) && fileName.toLowerCase(Locale.ROOT).endsWith(".chm");
    }

    /**
     * Validates that an index profile with the given id exists.
     *
     * @param profileId the profile identifier
     * @throws IllegalArgumentException if the profile does not exist
     */
    private void validateIndexProfileExists(String profileId) {
        if (profileId != null && profileRepository.findIndexById(profileId).isEmpty()) {
            throw new IllegalArgumentException("Index profile not found: " + profileId);
        }
    }

    /**
     * Validates that a retrieval profile with the given id exists.
     *
     * @param profileId the profile identifier
     * @throws IllegalArgumentException if the profile does not exist
     */
    private void validateRetrievalProfileExists(String profileId) {
        if (profileId != null && profileRepository.findRetrievalById(profileId).isEmpty()) {
            throw new IllegalArgumentException("Retrieval profile not found: " + profileId);
        }
    }

    /**
     * Validates that an index profile can be bound to the given source.
     *
     * @param profileId the index profile identifier
     * @param sourceId the source identifier
     * @throws IllegalArgumentException if the profile is not found
     * @throws ApiConflictException if the profile cannot be bound to the source
     */
    private void validateIndexProfileBindableToSource(String profileId, String sourceId) {
        validateIndexProfileExists(profileId);
        if (profileId == null) {
            return;
        }
        ProfileRepository.ProfileRecord profile = profileRepository.findIndexById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Index profile not found: " + profileId));
        ensureProfileBindableToSource(profile, sourceId, "index");
    }

    /**
     * Validates that a retrieval profile can be bound to the given source.
     *
     * @param profileId the retrieval profile identifier
     * @param sourceId the source identifier
     * @throws IllegalArgumentException if the profile is not found
     * @throws ApiConflictException if the profile cannot be bound to the source
     */
    private void validateRetrievalProfileBindableToSource(String profileId, String sourceId) {
        validateRetrievalProfileExists(profileId);
        if (profileId == null) {
            return;
        }
        ProfileRepository.ProfileRecord profile = profileRepository.findRetrievalById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Retrieval profile not found: " + profileId));
        ensureProfileBindableToSource(profile, sourceId, "retrieval");
    }

    /**
     * Ensures the given profile can be bound to the specified source.
     *
     * @param profile the profile record
     * @param sourceId the source identifier
     * @param profileType the profile type (index or retrieval)
     * @throws ApiConflictException if the profile is not bindable
     */
    private void ensureProfileBindableToSource(ProfileRepository.ProfileRecord profile, String sourceId, String profileType) {
        if (profile.readonly()) {
            return;
        }
        if (sourceId.equals(profile.ownerSourceId())) {
            return;
        }
        throw new ApiConflictException(
            "PROFILE_BINDING_NOT_ALLOWED",
            "Only system default or source-owned " + profileType + " profiles can be bound to source " + sourceId
        );
    }

    /**
     * Ensures the given profile is mutable (not read-only).
     *
     * @param profile the profile record
     * @param profileType the profile type (index or retrieval)
     * @throws ApiConflictException if the profile is read-only
     */
    private void ensureProfileMutable(ProfileRepository.ProfileRecord profile, String profileType) {
        if (profile.readonly()) {
            throw new ApiConflictException(
                "READ_ONLY_PROFILE",
                "System default " + profileType + " profile is read-only. Customize it from the source config page instead."
            );
        }
    }

    /**
     * Ensures the specified profile is not currently bound to any source.
     *
     * @param profileId the profile identifier
     * @param indexProfile true if the profile is an index profile, false if retrieval
     * @throws IllegalStateException if the profile is still bound to a source
     */
    /**
     * Ensures the given profile is not currently bound to any source.
     *
     * @param profileId the profile identifier
     * @param indexProfile true for index profile, false for retrieval profile
     * @throws IllegalStateException if the profile is still bound to a source
     */
    private void ensureProfileNotBound(String profileId, boolean indexProfile) {
        boolean inUse = bindingRepository.findAll().stream().anyMatch(binding ->
            indexProfile ? profileId.equals(binding.indexProfileId()) : profileId.equals(binding.retrievalProfileId())
        );
        if (inUse) {
            throw new IllegalStateException("Profile is still bound to a source: " + profileId);
        }
    }

    /**
     * Deletes a source-owned profile if it is present and eligible for deletion.
     *
     * @param profile the profile record to potentially delete
     * @param indexProfile true if the profile is an index profile, false if retrieval
     * @param sourceId the source identifier that owns the profile
     * @param defaultProfileId the default profile identifier that should not be deleted
     */
    /**
     * Deletes a source-owned profile if it is not the default and not read-only.
     *
     * @param profile the profile record
     * @param indexProfile true for index profile, false for retrieval profile
     * @param sourceId the source identifier
     * @param defaultProfileId the default profile identifier
     */
    private void deleteOwnedProfileIfPresent(
        ProfileRepository.ProfileRecord profile,
        boolean indexProfile,
        String sourceId,
        String defaultProfileId
    ) {
        if (profile == null || profile.id().equals(defaultProfileId)) {
            return;
        }
        if (profile.readonly() || !sourceId.equals(profile.ownerSourceId())) {
            return;
        }
        if (indexProfile) {
            profileRepository.deleteIndex(profile.id());
        } else {
            profileRepository.deleteRetrieval(profile.id());
        }
    }

    /**
     * Determines whether a profile should be forked for a specific source.
     *
     * @param profile the profile record to evaluate
     * @param sourceId the source identifier requesting the profile
     * @param defaultProfileId the system default profile identifier
     * @return true if the profile should be forked, false otherwise
     */
    /**
     * Determines whether a profile should be forked (copied) for a specific source.
     *
     * @param profile the profile record
     * @param sourceId the source identifier
     * @param defaultProfileId the default profile identifier
     * @return true if the profile should be forked for the source
     */
    private boolean shouldForkProfileForSource(ProfileRepository.ProfileRecord profile, String sourceId, String defaultProfileId) {
        if (profile.readonly()) {
            return true;
        }
        if (profile.id().equals(defaultProfileId)) {
            return true;
        }
        return !sourceId.equals(profile.ownerSourceId());
    }

    /**
     * Resolves the name for a source-owned profile based on the request and fallback.
     *
     * @param source the source record that owns the profile
     * @param profileType the type of profile (e.g., "index" or "retrieval")
     * @param requestedName the name requested by the caller, may be null
     * @param fallbackName the fallback name to use if no valid request name is provided
     * @param forNewSourceOwnedProfile true if this is for a newly created source-owned profile
     * @return the resolved profile name
     */
    /**
     * Generates a name for a source-owned profile.
     *
     * @param source the source record
     * @param profileType the profile type (index or retrieval)
     * @param requestedName the requested name, may be null
     * @param fallbackName the fallback name, may be null
     * @param forNewSourceOwnedProfile true if creating a new source-owned profile
     * @return the resolved profile name
     */
    private String sourceOwnedProfileName(
        SourceRepository.SourceRecord source,
        String profileType,
        String requestedName,
        String fallbackName,
        boolean forNewSourceOwnedProfile
    ) {
        String normalizedRequestedName = requestedName != null ? requestedName.trim() : "";
        if (!normalizedRequestedName.isBlank()) {
            if (!normalizedRequestedName.startsWith("system-default-")) {
                return normalizedRequestedName;
            }
        }
        if (!forNewSourceOwnedProfile && fallbackName != null && !fallbackName.isBlank()) {
            return fallbackName;
        }
        return source.id() + "-" + profileType + "-profile";
    }

    /**
     * Ensures the specified profile name is available for use.
     *
     * @param profileName the profile name to check
     * @param currentProfileId the identifier of the current profile being updated, may be null
     * @param indexProfile true if checking an index profile, false if retrieval
     * @throws ApiConflictException if the profile name is already in use by another profile
     */
    /**
     * Ensures the given profile name is available (not already in use by another profile).
     *
     * @param profileName the profile name to check
     * @param currentProfileId the current profile identifier (excluded from check), may be null
     * @param indexProfile true for index profile, false for retrieval profile
     * @throws ApiConflictException if the name is already taken
     */
    private void ensureProfileNameAvailable(String profileName, String currentProfileId, boolean indexProfile) {
        if (profileName == null || profileName.isBlank()) {
            return;
        }
        Optional<ProfileRepository.ProfileRecord> existing = indexProfile
            ? profileRepository.findIndexByName(profileName)
            : profileRepository.findRetrievalByName(profileName);
        if (existing.isPresent() && !existing.get().id().equals(currentProfileId)) {
            throw new ApiConflictException("PROFILE_NAME_ALREADY_EXISTS", "Profile name already exists: " + profileName);
        }
    }

    /**
     * Validates the index profile configuration values.
     *
     * @param config the profile configuration map
     * @throws IllegalStateException if any config value is invalid
     */
    private void validateIndexProfileConfig(Map<String, Object> config) {
        if (config == null) {
            return;
        }
        Object indexing = config.get("indexing");
        if (!(indexing instanceof Map<?, ?>)) {
            return;
        }
        Map<?, ?> indexingMap = (Map<?, ?>) indexing;
        validateBoostValues(indexingMap);
        validateBm25Values(indexingMap);
    }

    /**
     * Validates that all boost values in the indexing config are non-negative.
     *
     * @param indexingMap the indexing configuration map
     * @throws IllegalStateException if any boost value is negative
     */
    private void validateBoostValues(Map<?, ?> indexingMap) {
        for (String key : new String[]{"titleBoost", "titlePathBoost", "keywordBoost", "contentBoost"}) {
            Object value = indexingMap.get(key);
            if (value instanceof Number && ((Number) value).doubleValue() < 0) {
                throw new IllegalStateException(key + " must be >= 0");
            }
        }
    }

    /**
     * Validates that BM25 parameters are non-negative.
     *
     * @param indexingMap the indexing configuration map
     * @throws IllegalStateException if BM25 parameters are negative
     */
    private void validateBm25Values(Map<?, ?> indexingMap) {
        Object bm25 = indexingMap.get("bm25");
        if (!(bm25 instanceof Map<?, ?>)) {
            return;
        }
        Map<?, ?> bm25Map = (Map<?, ?>) bm25;
        Object k1 = bm25Map.get("k1");
        if (k1 instanceof Number && ((Number) k1).doubleValue() < 0) {
            throw new IllegalStateException("BM25 k1 must be >= 0");
        }
        Object b = bm25Map.get("b");
        if (b instanceof Number && ((Number) b).doubleValue() < 0) {
            throw new IllegalStateException("BM25 b must be >= 0");
        }
    }

    /**
     * Creates a copy of the source record with updated profile bindings.
     *
     * @param source the source record
     * @param indexProfileId the new index profile identifier
     * @param retrievalProfileId the new retrieval profile identifier
     * @param rebuildRequired whether rebuild is required
     * @param now the current timestamp
     * @return the updated source record
     */
    private SourceRepository.SourceRecord withBoundProfiles(
        SourceRepository.SourceRecord source,
        String indexProfileId,
        String retrievalProfileId,
        boolean rebuildRequired,
        Instant now
    ) {
        return new SourceRepository.SourceRecord(
            source.id(),
            source.name(),
            source.description(),
            source.status(),
            source.storageMode(),
            indexProfileId,
            retrievalProfileId,
            source.runtimeStatus(),
            source.runtimeMessage(),
            source.currentJobId(),
            source.lastJobError(),
            rebuildRequired,
            source.createdAt(),
            now
        );
    }

    /**
     * Refreshes the chunk statistics for a document after chunk changes.
     *
     * @param documentId the document identifier
     * @throws IllegalArgumentException if the document is not found
     */
    private void refreshDocumentChunkStats(String documentId) {
        DocumentRepository.DocumentRecord existing = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        List<ChunkRepository.ChunkRecord> chunks = chunkRepository.findByDocumentId(documentId);
        int userEdited = (int) chunks.stream().filter(c -> "USER_EDITED".equals(c.editStatus())).count();
        documentRepository.update(new DocumentRepository.DocumentRecord(
            existing.id(), existing.sourceId(), existing.name(), existing.originalFilename(), existing.title(), existing.description(),
            existing.tags(), existing.sha256(), existing.contentType(), existing.language(), "INDEXED", "INDEXED",
            existing.fileSizeBytes(), chunks.size(), userEdited, existing.errorMessage(), existing.updatedBy(), existing.createdAt(), Instant.now()
        ));
    }

    /**
     * Computes the SHA-256 hash of an input stream.
     *
     * @param inputStream the input stream to hash
     * @return the hex-encoded SHA-256 digest
     * @throws IllegalStateException if hashing fails
     */
    private String sha256(InputStream inputStream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate sha256", e);
        }
    }

    /**
     * Computes the SHA-256 hash of a string value.
     *
     * @param value the string to hash
     * @return the hex-encoded SHA-256 digest
     * @throws IllegalStateException if hashing fails
     */
    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest((value == null ? "" : value).getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Filters chunks by source ids, document ids, and content type filters.
     *
     * @param sourceIds optional source identifiers to filter by
     * @param documentIds optional document identifiers to filter by
     * @param filters optional search filters including content types
     * @return the filtered list of searchable chunks
     */
    private List<SearchService.SearchableChunk> filterChunks(
        List<String> sourceIds,
        List<String> documentIds,
        RetrievalController.SearchFilters filters
    ) {
        return chunkRepository.findAll().stream()
            .filter(c -> sourceIds == null || sourceIds.isEmpty() || sourceIds.contains(c.sourceId()))
            .filter(c -> documentIds == null || documentIds.isEmpty() || documentIds.contains(c.documentId()))
            .filter(c -> {
                if (filters == null || filters.contentTypes() == null || filters.contentTypes().isEmpty()) {
                    return true;
                }
                return documentRepository.findById(c.documentId())
                    .map(DocumentRepository.DocumentRecord::contentType)
                    .filter(filters.contentTypes()::contains)
                    .isPresent();
            })
            .map(this::toSearchableChunk)
            .toList();
    }

    /**
     * Converts a chunk record to a searchable chunk.
     *
     * @param record the chunk record
     * @return the searchable chunk
     */
    private SearchService.SearchableChunk toSearchableChunk(ChunkRepository.ChunkRecord record) {
        return new SearchService.SearchableChunk(
            record.id(), record.documentId(), record.sourceId(), record.title(), record.titlePath(),
            record.keywords(), record.text(), record.markdown(), record.pageFrom(), record.pageTo(),
            record.ordinal(), record.editStatus(), record.updatedBy()
        );
    }

    /**
     * Ensures the source with the given id is writable (not in maintenance or error state).
     *
     * @param sourceId the source identifier
     * @throws IllegalArgumentException if the source is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    private void ensureSourceWritable(String sourceId) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        ensureSourceWritable(source);
    }

    /**
     * Ensures the given source record is writable (not in maintenance or error state).
     *
     * @param source the source record
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    private void ensureSourceWritable(SourceRepository.SourceRecord source) {
        if ("MAINTENANCE".equals(source.runtimeStatus())) {
            throw new ApiConflictException("SOURCE_IN_MAINTENANCE", "当前知识库正在重建，暂不可执行该操作");
        }
        if ("ERROR".equals(source.runtimeStatus())) {
            throw new ApiConflictException("SOURCE_UNAVAILABLE", "当前知识库处于异常状态，请重新触发重建");
        }
    }

    /**
     * Ensures the source with the given id is readable (not in maintenance or error state).
     *
     * @param sourceId the source identifier
     * @throws IllegalArgumentException if the source is not found
     * @throws ApiConflictException if the source is in maintenance or error state
     */
    private void ensureSourceReadable(String sourceId) {
        SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
        if ("MAINTENANCE".equals(source.runtimeStatus())) {
            throw new ApiConflictException("SOURCE_IN_MAINTENANCE", "当前知识库正在重建，暂不可执行该操作");
        }
        if ("ERROR".equals(source.runtimeStatus())) {
            throw new ApiConflictException("SOURCE_UNAVAILABLE", "当前知识库处于异常状态，请重新触发重建");
        }
    }

    /**
     * Ensures all sources in the given set are readable.
     *
     * @param sourceIds the set of source identifiers
     * @throws IllegalArgumentException if a source is not found
     * @throws ApiConflictException if a source is in maintenance or error state
     */
    private void ensureSourcesReadable(Set<String> sourceIds) {
        for (String sourceId : sourceIds) {
            ensureSourceReadable(sourceId);
        }
    }

    /**
     * Resolves the set of source ids referenced by the given source and document ids.
     *
     * @param sourceIds optional source identifiers
     * @param documentIds optional document identifiers
     * @return the resolved set of source identifiers
     */
    private Set<String> resolveReferencedSourceIds(List<String> sourceIds, List<String> documentIds) {
        LinkedHashSet<String> resolved = new LinkedHashSet<>();
        if (sourceIds != null) {
            resolved.addAll(sourceIds.stream().filter(StringUtils::hasText).toList());
        }
        if (documentIds != null) {
            documentIds.stream()
                .filter(StringUtils::hasText)
                .map(documentRepository::findById)
                .flatMap(Optional::stream)
                .map(DocumentRepository.DocumentRecord::sourceId)
                .forEach(resolved::add);
        }
        return resolved;
    }

    /**
     * Creates a copy of the source record with updated runtime state.
     *
     * @param source the source record
     * @param runtimeStatus the runtime status
     * @param runtimeMessage the runtime message
     * @param currentJobId the current job identifier
     * @param lastJobError the last job error message
     * @param rebuildRequired whether rebuild is required
     * @param updatedAt the updated timestamp
     * @return the source record with updated runtime state
     */
    private SourceRepository.SourceRecord withRuntimeState(
        SourceRepository.SourceRecord source,
        String runtimeStatus,
        String runtimeMessage,
        String currentJobId,
        String lastJobError,
        boolean rebuildRequired,
        Instant updatedAt
    ) {
        return new SourceRepository.SourceRecord(
            source.id(), source.name(), source.description(), source.status(), source.storageMode(),
            source.indexProfileId(), source.retrievalProfileId(), runtimeStatus, runtimeMessage, currentJobId,
            lastJobError, rebuildRequired, source.createdAt(), updatedAt
        );
    }

    /**
     * Marks all sources bound to the given index profile as requiring a rebuild.
     *
     * @param profileId the index profile identifier
     */
    private void markSourcesRebuildRequiredByIndexProfile(String profileId) {
        Instant now = Instant.now();
        bindingRepository.findAll().stream()
            .filter(binding -> profileId.equals(binding.indexProfileId()))
            .map(BindingRepository.BindingRecord::sourceId)
            .map(sourceRepository::findById)
            .flatMap(Optional::stream)
            .forEach(source -> sourceRepository.update(withRuntimeState(
                source,
                source.runtimeStatus(),
                source.runtimeMessage(),
                source.currentJobId(),
                source.lastJobError(),
                true,
                now
            )));
    }

    /**
     * Executes the source rebuild job, re-parsing and re-indexing all documents.
     *
     * @param jobId the job identifier
     * @param sourceId the source identifier
     */
    private void runSourceRebuild(String jobId, String sourceId) {
        Instant startedAt = Instant.now();
        putMdcIfText(LoggingKeys.JOB_ID, jobId);
        putMdcIfText(LoggingKeys.SOURCE_ID, sourceId);
        try {
            log.info("Starting source rebuild sourceId={} jobId={}", sourceId, jobId);
            SourceRepository.SourceRecord source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("Source not found: " + sourceId));
            List<DocumentRepository.DocumentRecord> documents = documentRepository.findBySourceId(sourceId);
            maintenanceJobFailureRepository.deleteByJobId(jobId);

            updateRebuildJob(jobId, sourceId, "RUNNING", "PREPARING", "Source rebuild started", documents.size(), 0, 0, 0, null, null, null, startedAt, null);

            updateRebuildJob(jobId, sourceId, "RUNNING", "CLEANING", "Cleaning existing chunks and indexes", documents.size(), 0, 0, 0, null, null, null, startedAt, null);
            lexicalIndexService.deleteSource(sourceId);
            vectorIndexService.deleteSource(sourceId);
            chunkRepository.deleteBySourceId(sourceId);
            storageManager.deleteRecursively(storageManager.artifactSourceDir(sourceId));

            int total = documents.size();
            int processed = 0;
            int succeededCount = 0;
            int failedCount = 0;
            for (DocumentRepository.DocumentRecord document : documents) {
                final String[] stageHolder = { "PARSING" };
                final int processedBeforeDocument = processed;
                final int succeededBeforeDocument = succeededCount;
                final int failedBeforeDocument = failedCount;
                try {
                    updateRebuildJob(jobId, sourceId, "RUNNING", stageHolder[0], "Parsing document", total, processedBeforeDocument, succeededBeforeDocument, failedBeforeDocument, document.id(), document.name(), null, startedAt, null);
                    putMdcIfText(LoggingKeys.DOCUMENT_ID, document.id());
                    rebuildDocumentFromOriginal(document, currentStage -> {
                        stageHolder[0] = currentStage;
                        updateRebuildJob(
                        jobId,
                        sourceId,
                        "RUNNING",
                        currentStage,
                        stageMessage(currentStage),
                        total,
                        processedBeforeDocument,
                        succeededBeforeDocument,
                        failedBeforeDocument,
                        document.id(),
                        document.name(),
                        null,
                        startedAt,
                        null
                    );
                    });
                    succeededCount++;
                } catch (Exception ex) {
                    failedCount++;
                    log.error(
                        "Failed rebuilding document sourceId={} jobId={} documentId={} stage={}",
                        sourceId,
                        jobId,
                        document.id(),
                        stageHolder[0],
                        ex
                    );
                    maintenanceJobFailureRepository.insert(new MaintenanceJobFailureRepository.FailureRecord(
                        Ids.newId("mjf"),
                        jobId,
                        sourceId,
                        document.id(),
                        document.name(),
                        stageHolder[0],
                        errorCodeFromException(ex),
                        summarizeError(ex),
                        Instant.now()
                    ));
                } finally {
                    removeMdcIfText(LoggingKeys.DOCUMENT_ID, document.id());
                }
                processed++;
                updateRebuildJob(jobId, sourceId, "RUNNING", processed == total ? "INDEXING" : "PARSING", "Rebuilt " + processed + "/" + total + " documents", total, processed, succeededCount, failedCount, null, null, failedCount > 0 ? failedCount + " 个文档处理失败" : null, startedAt, null);
            }
            Instant now = Instant.now();
            final int finalFailedCount = failedCount;
            final int finalSucceededCount = succeededCount;
            JobRepository.JobRecord succeeded = new JobRepository.JobRecord(
                jobId, "SOURCE_REBUILD", sourceId, null, finalFailedCount > 0 ? "FAILED" : "SUCCEEDED", 100, "COMPLETED",
                finalFailedCount > 0 ? "Source rebuild completed with failures" : "Source rebuild completed",
                "admin", total, total, finalSucceededCount, finalFailedCount, null, null,
                finalFailedCount > 0 ? finalFailedCount + " 个文档处理失败" : null, startedAt, now, startedAt, now
            );
            jobRepository.update(succeeded);
            sourceRepository.findById(sourceId).ifPresent(current -> sourceRepository.update(withRuntimeState(
                current,
                finalFailedCount > 0 ? "ERROR" : "ACTIVE",
                finalFailedCount > 0 ? "知识库重建失败，请重新触发重建" : null,
                null,
                finalFailedCount > 0 ? finalFailedCount + " 个文档处理失败" : null,
                finalFailedCount > 0,
                now
            )));
            log.info(
                "Completed source rebuild sourceId={} jobId={} totalDocuments={} succeededDocuments={} failedDocuments={}",
                sourceId,
                jobId,
                total,
                finalSucceededCount,
                finalFailedCount
            );
        } catch (Exception ex) {
            Instant now = Instant.now();
            jobRepository.update(new JobRepository.JobRecord(
                jobId, "SOURCE_REBUILD", sourceId, null, "FAILED", 0, "COMPLETED", ex.getMessage(),
                "admin", 0, 0, 0, 0, null, null, summarizeError(ex), startedAt, now, startedAt, now
            ));
            sourceRepository.findById(sourceId).ifPresent(current -> sourceRepository.update(withRuntimeState(
                current,
                "ERROR",
                "知识库重建失败，请重新触发重建",
                null,
                summarizeError(ex),
                true,
                now
            )));
            log.error("Source rebuild failed sourceId={} jobId={}", sourceId, jobId, ex);
        } finally {
            removeMdcIfText(LoggingKeys.SOURCE_ID, sourceId);
            removeMdcIfText(LoggingKeys.JOB_ID, jobId);
        }
    }

    /**
     * Rebuilds a single document from its original upload, re-parsing, chunking, and indexing.
     *
     * @param document the document record
     * @param stageCallback callback to report current rebuild stage
     */
    private void rebuildDocumentFromOriginal(DocumentRepository.DocumentRecord document, java.util.function.Consumer<String> stageCallback) {
        Path originalPath = storageManager.originalFilePath(document.sourceId(), document.id(), document.originalFilename());
        stageCallback.accept("PARSING");
        TikaConversionService.ConversionResult conversion = conversionService.convert(originalPath);
        Path artifactDir = storageManager.artifactDir(document.sourceId(), document.id());
        storageManager.writeString(artifactDir.resolve("content.md"), conversion.markdown());

        Instant now = Instant.now();
        DocumentRepository.DocumentRecord updatedDocument = new DocumentRepository.DocumentRecord(
            document.id(), document.sourceId(), document.name(), document.originalFilename(),
            conversion.title(), document.description(), document.tags(), document.sha256(),
            resolvePersistedContentType(document.contentType(), conversion.contentType(), document.originalFilename()),
            document.language(), "INDEXED", "INDEXED", document.fileSizeBytes(), 0, 0,
            null, "system", document.createdAt(), now
        );
        documentRepository.update(updatedDocument);

        stageCallback.accept("CHUNKING");
        List<ChunkingService.ChunkDraft> chunks = chunkingService.chunk(conversion.title(), conversion.text(), conversion.markdown());
        List<SearchService.SearchableChunk> insertedChunks = new ArrayList<>();
        for (ChunkingService.ChunkDraft draft : chunks) {
            ChunkRepository.ChunkRecord chunkRecord = new ChunkRepository.ChunkRecord(
                Ids.newId("chk"), document.id(), document.sourceId(), draft.ordinal(), draft.title(), draft.titlePath(), draft.keywords(),
                draft.text(), draft.markdown(), 1, 1, draft.tokenCount(), draft.textLength(), hash(draft.text() + draft.markdown()),
                "SYSTEM_GENERATED", "system", now, now
            );
            chunkRepository.insert(chunkRecord);
            insertedChunks.add(toSearchableChunk(chunkRecord));
        }
        stageCallback.accept("INDEXING");
        Map<String, List<Double>> vectors = embeddingService.ensureChunkEmbeddings(insertedChunks);
        lexicalIndexService.upsertChunks(insertedChunks);
        vectorIndexService.upsertChunks(insertedChunks, vectors);
        refreshDocumentChunkStats(document.id());
        if (log.isDebugEnabled()) {
            log.debug(
                "Rebuilt document sourceId={} documentId={} chunkCount={}",
                document.sourceId(),
                document.id(),
                insertedChunks.size()
            );
        }
    }

    /**
     * Puts a value into MDC if the value has text.
     *
     * @param key the MDC key
     * @param value the MDC value
     */
    private void putMdcIfText(String key, String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(key, value);
        }
    }

    /**
     * Removes a value from MDC if the value is null or has text.
     *
     * @param key the MDC key
     * @param value the MDC value to check
     */
    private void removeMdcIfText(String key, String value) {
        if (value == null || StringUtils.hasText(value)) {
            MDC.remove(key);
        }
    }

    /**
     * Counts the number of items in a list, returning 0 for null.
     *
     * @param items the list of items
     * @return the item count, or 0 if null
     */
    private int countItems(List<?> items) {
        return items == null ? 0 : items.size();
    }

    /**
     * Describes a query string for logging, optionally redacting the full text.
     *
     * @param query the raw query string
     * @return the described query for logging
     */
    private String describeQuery(String query) {
        String normalized = query == null ? "" : query.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            return "<blank>";
        }
        if (knowledgeLoggingProperties.isIncludeQueryText()) {
            return normalized.length() > 200 ? normalized.substring(0, 200) + "..." : normalized;
        }
        return "len=" + normalized.length() + ",hash=" + hash(normalized).substring(0, 12);
    }

    /**
     * Updates the rebuild job record with current progress and status.
     *
     * @param jobId the job identifier
     * @param sourceId the source identifier
     * @param status the job status
     * @param stage the current stage
     * @param message the status message
     * @param totalDocuments the total number of documents
     * @param processedDocuments the number of processed documents
     * @param successDocuments the number of successfully processed documents
     * @param failedDocuments the number of failed documents
     * @param currentDocumentId the current document identifier
     * @param currentDocumentName the current document name
     * @param errorSummary the error summary
     * @param startedAt the start timestamp
     * @param finishedAt the finish timestamp
     */
    private void updateRebuildJob(
        String jobId,
        String sourceId,
        String status,
        String stage,
        String message,
        int totalDocuments,
        int processedDocuments,
        int successDocuments,
        int failedDocuments,
        String currentDocumentId,
        String currentDocumentName,
        String errorSummary,
        Instant startedAt,
        Instant finishedAt
    ) {
        int progress = totalDocuments == 0 ? ("COMPLETED".equals(stage) ? 100 : 0) : Math.min(99, (processedDocuments * 100) / totalDocuments);
        if (finishedAt != null) {
            progress = 100;
        }
        jobRepository.update(new JobRepository.JobRecord(
            jobId,
            "SOURCE_REBUILD",
            sourceId,
            null,
            status,
            progress,
            stage,
            message,
            "admin",
            totalDocuments,
            processedDocuments,
            successDocuments,
            failedDocuments,
            currentDocumentId,
            currentDocumentName,
            errorSummary,
            startedAt,
            finishedAt,
            startedAt,
            Instant.now()
        ));
    }

    /**
     * Returns a human-readable message for a rebuild stage.
     *
     * @param stage the stage name
     * @return the stage message
     */
    private String stageMessage(String stage) {
        return switch (stage) {
        case "CLEANING" -> "Cleaning existing chunks and indexes";
        case "PARSING" -> "Parsing document";
        case "CHUNKING" -> "Rebuilding chunks";
        case "INDEXING" -> "Rebuilding indexes";
        case "COMPLETED" -> "Source rebuild completed";
        default -> "Preparing source rebuild";
        };
    }

    /**
     * Summarizes an exception by extracting the root cause message.
     *
     * @param ex the exception
     * @return the summarized error message
     */
    private String summarizeError(Exception ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return StringUtils.hasText(current.getMessage()) ? current.getMessage() : ex.getClass().getSimpleName();
    }

    /**
     * Maps an exception to an error code based on its message content.
     *
     * @param ex the exception
     * @return the error code string
     */
    private String errorCodeFromException(Exception ex) {
        String message = summarizeError(ex).toLowerCase(Locale.ROOT);
        if (message.contains("parse") || message.contains("convert")) {
            return "DOCUMENT_PARSE_FAILED";
        }
        if (message.contains("chunk")) {
            return "CHUNK_BUILD_FAILED";
        }
        if (message.contains("index")) {
            return "INDEX_WRITE_FAILED";
        }
        if (message.contains("embed")) {
            return "EMBEDDING_FAILED";
        }
        return "REBUILD_DOCUMENT_FAILED";
    }

    /**
     * Converts a profile record to a profile summary.
     *
     * @param profile the profile record
     * @return the profile summary
     */
    private ProfileController.ProfileSummary toProfileSummary(ProfileRepository.ProfileRecord profile) {
        return new ProfileController.ProfileSummary(
            profile.id(),
            profile.name(),
            profileScope(profile),
            profile.readonly(),
            profile.ownerSourceId(),
            profile.derivedFromProfileId(),
            profile.config(),
            profile.createdAt(),
            profile.updatedAt()
        );
    }

    /**
     * Converts a profile record to a profile detail.
     *
     * @param profile the profile record
     * @return the profile detail
     */
    private ProfileController.ProfileDetail toProfileDetail(ProfileRepository.ProfileRecord profile) {
        return new ProfileController.ProfileDetail(
            profile.id(),
            profile.name(),
            profileScope(profile),
            profile.readonly(),
            profile.ownerSourceId(),
            profile.derivedFromProfileId(),
            profile.config(),
            profile.createdAt(),
            profile.updatedAt()
        );
    }

    /**
     * Converts a source and profile record to a source profile config response.
     *
     * @param source the source record
     * @param profile the profile record
     * @param createdFromDefault true if the profile was created from the default
     * @return the source profile config response
     */
    private SourceController.SourceProfileConfigResponse toSourceProfileConfigResponse(
        SourceRepository.SourceRecord source,
        ProfileRepository.ProfileRecord profile,
        boolean createdFromDefault
    ) {
        return new SourceController.SourceProfileConfigResponse(
            source.id(),
            profile.id(),
            profile.name(),
            profileScope(profile),
            profile.readonly(),
            profile.ownerSourceId(),
            profile.derivedFromProfileId(),
            profile.config(),
            source.rebuildRequired(),
            createdFromDefault,
            profile.createdAt(),
            profile.updatedAt()
        );
    }

    /**
     * Determines the scope of a profile (system or source-owned).
     *
     * @param profile the profile record
     * @return "system" or "source"
     */
    private String profileScope(ProfileRepository.ProfileRecord profile) {
        return profile.ownerSourceId() == null || profile.ownerSourceId().isBlank() ? "system" : "source";
    }

    /**
     * Converts a source record to a source response.
     *
     * @param source the source record
     * @return the source response
     */
    private SourceController.SourceResponse toSourceResponse(SourceRepository.SourceRecord source) {
        return new SourceController.SourceResponse(
            source.id(), source.name(), source.description(), source.status(), source.storageMode(),
            source.indexProfileId(), source.retrievalProfileId(), source.runtimeStatus(), source.runtimeMessage(),
            source.currentJobId(), source.lastJobError(), source.rebuildRequired(), source.createdAt(), source.updatedAt()
        );
    }

    /**
     * Converts a document record to a document summary.
     *
     * @param document the document record
     * @return the document summary
     */
    private DocumentController.DocumentSummary toDocumentSummary(DocumentRepository.DocumentRecord document) {
        return new DocumentController.DocumentSummary(
            document.id(), document.sourceId(), document.name(), document.contentType(), document.title(), document.status(),
            document.indexStatus(), document.fileSizeBytes(), document.chunkCount(), document.userEditedChunkCount(),
            document.createdAt(), document.updatedAt()
        );
    }

    /**
     * Converts a document record to a document detail.
     *
     * @param document the document record
     * @return the document detail
     */
    private DocumentController.DocumentDetail toDocumentDetail(DocumentRepository.DocumentRecord document) {
        return new DocumentController.DocumentDetail(
            document.id(), document.sourceId(), document.name(), document.originalFilename(), document.title(), document.description(),
            document.tags(), document.sha256(), document.contentType(), document.language(), document.status(), document.indexStatus(),
            document.fileSizeBytes(), document.chunkCount(), document.userEditedChunkCount(), document.errorMessage(),
            document.createdAt(), document.updatedAt()
        );
    }

    /**
     * Converts a chunk record to a chunk summary with a truncated snippet.
     *
     * @param chunk the chunk record
     * @return the chunk summary
     */
    private ChunkController.ChunkSummary toChunkSummary(ChunkRepository.ChunkRecord chunk) {
        String snippet = chunk.text().length() > 180 ? chunk.text().substring(0, 180) : chunk.text();
        return new ChunkController.ChunkSummary(
            chunk.id(), chunk.documentId(), chunk.sourceId(), chunk.ordinal(), chunk.title(), chunk.titlePath(),
            chunk.keywords(), snippet, chunk.pageFrom(), chunk.pageTo(), chunk.tokenCount(), chunk.editStatus(), chunk.updatedAt()
        );
    }

    /**
     * Converts a chunk record to a chunk detail.
     *
     * @param chunk the chunk record
     * @return the chunk detail
     */
    private ChunkController.ChunkDetail toChunkDetail(ChunkRepository.ChunkRecord chunk) {
        return new ChunkController.ChunkDetail(
            chunk.id(), chunk.documentId(), chunk.sourceId(), chunk.ordinal(), chunk.title(), chunk.titlePath(), chunk.keywords(),
            chunk.text(), chunk.markdown(), chunk.pageFrom(), chunk.pageTo(), chunk.tokenCount(), chunk.textLength(),
            chunk.editStatus(), chunk.updatedBy(), chunk.createdAt(), chunk.updatedAt()
        );
    }

    /**
     * Converts a job record to a job response.
     *
     * @param job the job record
     * @return the job response
     */
    private JobController.JobResponse toJobResponse(JobRepository.JobRecord job) {
        return new JobController.JobResponse(
            job.id(), job.jobType(), job.sourceId(), job.documentId(), job.status(), job.progress(), job.stage(), job.message(),
            job.createdBy(), job.totalDocuments(), job.processedDocuments(), job.successDocuments(), job.failedDocuments(),
            job.currentDocumentId(), job.currentDocumentName(), job.errorSummary(),
            job.startedAt(), job.finishedAt(), job.createdAt(), job.updatedAt()
        );
    }

    /**
     * Converts a job record to a maintenance job summary.
     *
     * @param job the job record, may be null
     * @return the maintenance job summary, or null if input is null
     */
    private SourceController.MaintenanceJobSummary toMaintenanceJobSummary(JobRepository.JobRecord job) {
        if (job == null) {
            return null;
        }
        return new SourceController.MaintenanceJobSummary(
            job.id(),
            job.jobType(),
            job.status(),
            job.stage(),
            job.createdBy(),
            job.startedAt(),
            job.updatedAt(),
            job.finishedAt(),
            job.totalDocuments(),
            job.processedDocuments(),
            job.successDocuments(),
            job.failedDocuments(),
            job.currentDocumentId(),
            job.currentDocumentName(),
            job.message(),
            job.errorSummary()
        );
    }

    /**
     * Merges two maps, with patch values overriding base values.
     *
     * @param base the base map
     * @param patch the patch map
     * @return the merged map
     */
    private Map<String, Object> mergeMaps(Map<String, Object> base, Map<String, Object> patch) {
        if (patch == null || patch.isEmpty()) {
            return base;
        }
        Map<String, Object> merged = new java.util.LinkedHashMap<>(base);
        merged.putAll(patch);
        return merged;
    }

    /**
     * Resolves retrieval settings by merging profile config, request overrides, and defaults.
     *
     * @param retrievalProfileId the retrieval profile identifier
     * @param requestTopK the requested topK value, may be null
     * @param override the search override, may be null
     * @return the resolved retrieval settings
     * @throws IllegalStateException if topK is invalid
     */
    private ResolvedRetrievalSettings resolveRetrievalSettings(
        String retrievalProfileId,
        Integer requestTopK,
        RetrievalController.SearchOverride override
    ) {
        KnowledgeProperties.Retrieval defaults = profileBootstrapService.properties().getRetrieval();
        Map<String, Object> profileConfig = retrievalProfileId == null
            ? Map.of()
            : profileRepository.findRetrievalById(retrievalProfileId).map(ProfileRepository.ProfileRecord::config).orElse(Map.of());

        String mode = firstNonBlank(
            override != null ? override.mode() : null,
            nestedString(profileConfig, "retrieval", "mode"),
            defaults.getMode()
        );
        int finalTopK = requestTopK != null
            ? requestTopK
            : nestedInt(profileConfig, "result", "finalTopK").orElse(defaults.getFinalTopK());
        if (finalTopK <= 0 || finalTopK > defaults.getMaxTopK()) {
            throw new IllegalStateException("Invalid topK: " + finalTopK);
        }

        int lexicalTopK = override != null && override.lexicalTopK() != null
            ? override.lexicalTopK()
            : nestedInt(profileConfig, "retrieval", "lexicalTopK").orElse(defaults.getLexicalTopK());
        int semanticTopK = override != null && override.semanticTopK() != null
            ? override.semanticTopK()
            : nestedInt(profileConfig, "retrieval", "semanticTopK").orElse(defaults.getSemanticTopK());
        int rrfK = override != null && override.rrfK() != null
            ? override.rrfK()
            : nestedInt(profileConfig, "retrieval", "rrfK").orElse(defaults.getRrfK());
        Double scoreThreshold = supportsThresholdForMode(mode)
            ? (override != null && override.scoreThreshold() != null
                ? override.scoreThreshold()
                : resolveProfileScoreThreshold(mode, profileConfig))
            : null;
        int snippetLength = override != null && override.snippetLength() != null
            ? override.snippetLength()
            : nestedInt(profileConfig, "result", "snippetLength").orElse(defaults.getSnippetLength());

        return new ResolvedRetrievalSettings(
            mode == null ? "hybrid" : mode,
            Math.max(lexicalTopK, finalTopK),
            Math.max(semanticTopK, finalTopK),
            finalTopK,
            Math.max(rrfK, 1),
            scoreThreshold != null ? clamp(scoreThreshold) : null,
            Math.max(snippetLength, 1)
        );
    }

    /**
     * Resolves the retrieval profile id to use for a search.
     *
     * @param explicitRetrievalProfileId the explicitly provided profile id, may be null
     * @param sourceIds the source identifiers
     * @return the resolved retrieval profile id
     */
    private String resolveSearchRetrievalProfileId(String explicitRetrievalProfileId, List<String> sourceIds) {
        if (explicitRetrievalProfileId != null && !explicitRetrievalProfileId.isBlank()) {
            return explicitRetrievalProfileId;
        }
        if (sourceIds != null && sourceIds.size() == 1) {
            return sourceRepository.findById(sourceIds.get(0))
                .map(SourceRepository.SourceRecord::retrievalProfileId)
                .orElse(profileBootstrapService.defaultRetrievalProfileId());
        }
        return profileBootstrapService.defaultRetrievalProfileId();
    }

    /**
     * Normalizes compare modes to a list of valid mode names.
     *
     * @param modes the requested modes, may be null
     * @return the normalized list of compare modes
     */
    private List<String> normalizeCompareModes(List<String> modes) {
        List<String> normalized = modes == null
            ? List.of()
            : modes.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(mode -> mode.equals("hybrid") || mode.equals("semantic") || mode.equals("lexical"))
                .distinct()
                .toList();
        if (!normalized.isEmpty()) {
            return normalized;
        }
        return List.of("hybrid", "semantic", "lexical");
    }

    /**
     * Extracts an integer value from a nested map structure.
     *
     * @param root the root map
     * @param parentKey the parent key
     * @param key the nested key
     * @return the integer value if present and numeric
     */
    private Optional<Integer> nestedInt(Map<String, Object> root, String parentKey, String key) {
        Object value = nestedValue(root, parentKey, key);
        if (value instanceof Number number) {
            return Optional.of(number.intValue());
        }
        return Optional.empty();
    }

    /**
     * Extracts a double value from a nested map structure.
     *
     * @param root the root map
     * @param parentKey the parent key
     * @param key the nested key
     * @return the double value if present and numeric
     */
    private Optional<Double> nestedDouble(Map<String, Object> root, String parentKey, String key) {
        Object value = nestedValue(root, parentKey, key);
        if (value instanceof Number number) {
            return Optional.of(number.doubleValue());
        }
        return Optional.empty();
    }

    /**
     * Resolves the score threshold for the given retrieval mode from profile config.
     *
     * @param mode the retrieval mode
     * @param profileConfig the profile configuration map
     * @return the score threshold, or null if not applicable
     */
    private Double resolveProfileScoreThreshold(String mode, Map<String, Object> profileConfig) {
        Optional<Double> legacyThreshold = nestedDouble(profileConfig, "retrieval", "scoreThreshold");
        return switch ((mode == null ? "hybrid" : mode).toLowerCase(Locale.ROOT)) {
        case "semantic" -> nestedDouble(profileConfig, "retrieval", "semanticThreshold").or(() -> legacyThreshold).orElse(null);
        case "lexical" -> nestedDouble(profileConfig, "retrieval", "lexicalThreshold").or(() -> legacyThreshold).orElse(null);
        default -> null;
        };
    }

    /**
     * Checks whether the given retrieval mode supports score thresholds.
     *
     * @param mode the retrieval mode
     * @return true if the mode supports thresholds
     */
    private boolean supportsThresholdForMode(String mode) {
        return "semantic".equalsIgnoreCase(mode) || "lexical".equalsIgnoreCase(mode);
    }

    /**
     * Extracts a non-blank string value from a nested map structure.
     *
     * @param root the root map
     * @param parentKey the parent key
     * @param key the nested key
     * @return the string value if present and non-blank, null otherwise
     */
    private String nestedString(Map<String, Object> root, String parentKey, String key) {
        Object value = nestedValue(root, parentKey, key);
        return value instanceof String string && StringUtils.hasText(string) ? string : null;
    }

    /**
     * Extracts a value from a nested map structure.
     *
     * @param root the root map
     * @param parentKey the parent key
     * @param key the nested key
     * @return the nested value, or null if not found
     */
    @SuppressWarnings("unchecked")
    private Object nestedValue(Map<String, Object> root, String parentKey, String key) {
        if (root == null || root.isEmpty()) {
            return null;
        }
        Object nested = root.get(parentKey);
        if (!(nested instanceof Map<?, ?> nestedMap)) {
            return null;
        }
        return ((Map<String, Object>) nestedMap).get(key);
    }

    /**
     * Returns the first non-blank string from the given values.
     *
     * @param values the values to check
     * @return the first non-blank value, or null if none
     */
    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * Clamps a double value to the range [0, 1].
     *
     * @param value the value to clamp
     * @return the clamped value
     */
    private double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    /**
     * Paginates a list of items.
     *
     * @param items the full list of items
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @return the paginated response
     */
    private <T> PageResponse<T> page(List<T> items, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        int from = Math.min((safePage - 1) * safePageSize, items.size());
        int to = Math.min(from + safePageSize, items.size());
        return new PageResponse<>(items.subList(from, to), safePage, safePageSize, items.size());
    }

    /**
     * Resolved retrieval settings combining profile config, overrides, and defaults.
     */
    private record ResolvedRetrievalSettings(
        String mode,
        int lexicalTopK,
        int semanticTopK,
        int finalTopK,
        int rrfK,
        Double scoreThreshold,
        int snippetLength
    ) {
        /**
         * Converts these resolved settings to search options.
         *
         * @return the search options
         */
        private SearchService.SearchOptions toSearchOptions() {
            return new SearchService.SearchOptions(
                mode,
                lexicalTopK,
                semanticTopK,
                finalTopK,
                rrfK,
                scoreThreshold
            );
        }

        /**
         * Returns a copy of these settings with the given mode and topK values.
         *
         * @param nextMode the new mode
         * @param nextFinalTopK the new final topK
         * @param nextScoreThreshold the new score threshold, may be null
         * @return the updated retrieval settings
         */
        private ResolvedRetrievalSettings withMode(String nextMode, int nextFinalTopK, Double nextScoreThreshold) {
            return new ResolvedRetrievalSettings(
                nextMode,
                Math.max(lexicalTopK, nextFinalTopK),
                Math.max(semanticTopK, nextFinalTopK),
                nextFinalTopK,
                rrfK,
                nextScoreThreshold,
                snippetLength
            );
        }
    }
}
