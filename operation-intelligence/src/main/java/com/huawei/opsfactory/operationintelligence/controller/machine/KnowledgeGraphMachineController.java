/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.operationintelligence.controller.base.BaseKnowledgeGraphController;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphEntity;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphOntology;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphSnapshot;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.service.KnowledgeGraphService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.apache.servicecomb.provider.rest.common.RestSchema;

import java.util.Map;

/**
 * Machine-to-machine REST controller for knowledge graph operations.
 * <p>
 * Requires Basic authentication for all endpoints.
 *
 * @author x00000000
 * @since 2026-05-20
 */
@RestController
@RestSchema(schemaId = "knowledgeGraphMachineController")
@RequestMapping("/machine/operation-intelligence/graph")
public class KnowledgeGraphMachineController extends BaseKnowledgeGraphController {

    /**
     * Constructs a KnowledgeGraphMachineController.
     *
     * @param knowledgeGraphService the knowledge graph service
     */
    public KnowledgeGraphMachineController(KnowledgeGraphService knowledgeGraphService) {
        super(knowledgeGraphService);
    }

    /**
     * Imports or updates ontology data.
     *
     * @param request the ontology request
     * @return the result
     */
    @Override
    @PostMapping("/ontologies")
    @BasicAuth
    public Map<String, Object> importOntology(@RequestBody GraphOntology request) {
        return super.importOntology(request);
    }

    /**
     * Lists ontologies.
     *
     * @return the result
     */
    @Override
    @GetMapping("/ontologies")
    @BasicAuth
    public Map<String, Object> listOntologies() {
        return super.listOntologies();
    }

    /**
     * Gets ontology detail.
     *
     * @param ontologyId the ontology identifier
     * @return the result
     */
    @Override
    @GetMapping("/ontologies/{ontologyId}")
    @BasicAuth
    public Map<String, Object> getOntology(@PathVariable("ontologyId") String ontologyId) {
        return super.getOntology(ontologyId);
    }

    /**
     * Lists graph entity environments under one ontology.
     *
     * @param ontologyId the optional ontology identifier
     * @return the result
     */
    @Override
    @GetMapping("/environments")
    @BasicAuth
    public Map<String, Object> listEnvironments(@RequestParam(value = "ontologyId", required = false) String ontologyId) {
        return super.listEnvironments(ontologyId);
    }

    /**
     * Deletes one ontology and all graph snapshots under it.
     *
     * @param ontologyId the ontology identifier
     * @return the result
     */
    @Override
    @DeleteMapping("/ontologies/{ontologyId}")
    @BasicAuth
    public Map<String, Object> deleteOntology(@PathVariable("ontologyId") String ontologyId) {
        return super.deleteOntology(ontologyId);
    }

    /**
     * Deletes one ontology and all graph snapshots under it via POST body.
     *
     * @param request the request containing ontologyId
     * @return the result
     */
    @Override
    @PostMapping("/admin/delete-ontology")
    @BasicAuth
    public Map<String, Object> deleteOntologyByPost(@RequestBody Map<String, Object> request) {
        return super.deleteOntologyByPost(request);
    }

    /**
     * Imports graph data.
     *
     * @param request the graph snapshot request
     * @return the result
     */
    @Override
    @PostMapping("/admin/import")
    @BasicAuth
    public Map<String, Object> importGraph(@RequestBody GraphSnapshot request) {
        return super.importGraph(request);
    }

    /**
     * Deletes graph entities for one environment.
     *
     * @param ontologyId the optional ontology identifier
     * @param envCode the environment code
     * @return the result
     */
    @Override
    @DeleteMapping("/admin/entities")
    @BasicAuth
    public Map<String, Object> deleteEntities(@RequestParam(value = "ontologyId", required = false) String ontologyId,
        @RequestParam("envCode") String envCode) {
        return super.deleteEntities(ontologyId, envCode);
    }

    /**
     * Deletes graph entities for one environment via POST body.
     *
     * @param request the request containing ontologyId and envCode
     * @return the result
     */
    @Override
    @PostMapping("/admin/delete-entities")
    @BasicAuth
    public Map<String, Object> deleteEntitiesByPost(@RequestBody Map<String, Object> request) {
        return super.deleteEntitiesByPost(request);
    }

    /**
     * Gets an entity.
     *
     * @param entityId the entity identifier
     * @param envCode the environment code
     * @param ontologyId the optional ontology identifier
     * @return the result
     */
    @Override
    @GetMapping("/entities/{entityId}")
    @BasicAuth
    public Map<String, Object> getEntity(@PathVariable("entityId") String entityId,
        @RequestParam("envCode") String envCode,
        @RequestParam(value = "ontologyId", required = false) String ontologyId) {
        return super.getEntity(ontologyId, envCode, entityId);
    }

    /**
     * Updates one entity in the current environment snapshot.
     *
     * @param entityId the entity identifier
     * @param request the request containing ontologyId, envCode and entity payload
     * @return the updated entity
     */
    @Override
    @PutMapping("/entities/{entityId}")
    @BasicAuth
    public Map<String, Object> updateEntity(@PathVariable("entityId") String entityId,
        @RequestBody Map<String, Object> request) {
        return super.updateEntity(entityId, request);
    }

    /**
     * Deletes one entity in the current environment snapshot.
     *
     * @param entityId the entity identifier
     * @param envCode the environment code
     * @param ontologyId the optional ontology identifier
     * @return the deletion result
     */
    @Override
    @DeleteMapping("/entities/{entityId}")
    @BasicAuth
    public Map<String, Object> deleteEntity(@PathVariable("entityId") String entityId,
        @RequestParam("envCode") String envCode,
        @RequestParam(value = "ontologyId", required = false) String ontologyId) {
        return super.deleteEntity(ontologyId, envCode, entityId);
    }

    /**
     * Gets graph resource tree.
     *
     * @param envCode the environment code
     * @param ontologyId the optional ontology identifier
     * @return the result
     */
    @Override
    @GetMapping("/resources/tree")
    @BasicAuth
    public Map<String, Object> getResourceTree(@RequestParam("envCode") String envCode,
        @RequestParam(value = "ontologyId", required = false) String ontologyId) {
        return super.getResourceTree(ontologyId, envCode);
    }

    /**
     * Queries a subgraph.
     *
     * @param request the request containing query parameters
     * @return the result
     */
    @Override
    @PostMapping("/subgraph")
    @BasicAuth
    public Map<String, Object> querySubgraph(@RequestBody Map<String, Object> request) {
        return super.querySubgraph(request);
    }

    /**
     * Queries observations.
     *
     * @param request the request containing query parameters
     * @return the result
     */
    @Override
    @PostMapping("/observations/query")
    @BasicAuth
    public Map<String, Object> queryObservations(@RequestBody Map<String, Object> request) {
        return super.queryObservations(request);
    }

    /**
     * Finds an impact path.
     *
     * @param request the request containing impact path parameters
     * @return the result
     */
    @Override
    @PostMapping("/impact-path")
    @BasicAuth
    public Map<String, Object> findImpactPath(@RequestBody Map<String, Object> request) {
        return super.findImpactPath(request);
    }

    /**
     * Gets root cause candidates.
     *
     * @param request the request containing query parameters
     * @return the result
     */
    @Override
    @PostMapping("/root-cause-candidates")
    @BasicAuth
    public Map<String, Object> getRootCauseCandidates(@RequestBody Map<String, Object> request) {
        return super.getRootCauseCandidates(request);
    }

    /**
     * Gets diagnosis context.
     *
     * @param request the request containing diagnosis parameters
     * @return the result
     */
    @Override
    @PostMapping("/diagnosis/context")
    @BasicAuth
    public Map<String, Object> getDiagnosisContext(@RequestBody Map<String, Object> request) {
        return super.getDiagnosisContext(request);
    }

    /**
     * Exports native graph data.
     *
     * @param request the request containing export parameters
     * @return the result
     */
    @Override
    @PostMapping("/admin/export")
    @BasicAuth
    public Map<String, Object> exportGraph(@RequestBody Map<String, Object> request) {
        return super.exportGraph(request);
    }
}
