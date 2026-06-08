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
 * Knowledge graph machine controller.
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

    @Override
    @PostMapping("/ontologies")
    @BasicAuth
    public Map<String, Object> importOntology(@RequestBody GraphOntology request) {
        return super.importOntology(request);
    }

    @Override
    @GetMapping("/ontologies")
    @BasicAuth
    public Map<String, Object> listOntologies() {
        return super.listOntologies();
    }

    @Override
    @GetMapping("/ontologies/{ontologyId}")
    @BasicAuth
    public Map<String, Object> getOntology(@PathVariable("ontologyId") String ontologyId) {
        return super.getOntology(ontologyId);
    }

    @Override
    @GetMapping("/environments")
    @BasicAuth
    public Map<String, Object> listEnvironments(@RequestParam(value = "ontologyId", required = false) String ontologyId) {
        return super.listEnvironments(ontologyId);
    }

    @Override
    @DeleteMapping("/ontologies/{ontologyId}")
    @BasicAuth
    public Map<String, Object> deleteOntology(@PathVariable("ontologyId") String ontologyId) {
        return super.deleteOntology(ontologyId);
    }

    @Override
    @PostMapping("/admin/delete-ontology")
    @BasicAuth
    public Map<String, Object> deleteOntologyByPost(@RequestBody Map<String, Object> request) {
        return super.deleteOntologyByPost(request);
    }

    @Override
    @PostMapping("/admin/import")
    @BasicAuth
    public Map<String, Object> importGraph(@RequestBody GraphSnapshot request) {
        return super.importGraph(request);
    }

    @Override
    @DeleteMapping("/admin/entities")
    @BasicAuth
    public Map<String, Object> deleteEntities(@RequestParam(value = "ontologyId", required = false) String ontologyId,
        @RequestParam("envCode") String envCode) {
        return super.deleteEntities(ontologyId, envCode);
    }

    @Override
    @PostMapping("/admin/delete-entities")
    @BasicAuth
    public Map<String, Object> deleteEntitiesByPost(@RequestBody Map<String, Object> request) {
        return super.deleteEntitiesByPost(request);
    }

    @Override
    @GetMapping("/entities/{entityId}")
    @BasicAuth
    public Map<String, Object> getEntity(@PathVariable("entityId") String entityId,
        @RequestParam("envCode") String envCode,
        @RequestParam(value = "ontologyId", required = false) String ontologyId) {
        return super.getEntity(ontologyId, envCode, entityId);
    }

    @Override
    @PutMapping("/entities/{entityId}")
    @BasicAuth
    public Map<String, Object> updateEntity(@PathVariable("entityId") String entityId,
        @RequestBody Map<String, Object> request) {
        return super.updateEntity(entityId, request);
    }

    @Override
    @DeleteMapping("/entities/{entityId}")
    @BasicAuth
    public Map<String, Object> deleteEntity(@PathVariable("entityId") String entityId,
        @RequestParam("envCode") String envCode,
        @RequestParam(value = "ontologyId", required = false) String ontologyId) {
        return super.deleteEntity(ontologyId, envCode, entityId);
    }

    @Override
    @GetMapping("/resources/tree")
    @BasicAuth
    public Map<String, Object> getResourceTree(@RequestParam("envCode") String envCode,
        @RequestParam(value = "ontologyId", required = false) String ontologyId) {
        return super.getResourceTree(ontologyId, envCode);
    }

    @Override
    @PostMapping("/subgraph")
    @BasicAuth
    public Map<String, Object> querySubgraph(@RequestBody Map<String, Object> request) {
        return super.querySubgraph(request);
    }

    @Override
    @PostMapping("/observations/query")
    @BasicAuth
    public Map<String, Object> queryObservations(@RequestBody Map<String, Object> request) {
        return super.queryObservations(request);
    }

    @Override
    @PostMapping("/impact-path")
    @BasicAuth
    public Map<String, Object> findImpactPath(@RequestBody Map<String, Object> request) {
        return super.findImpactPath(request);
    }

    @Override
    @PostMapping("/root-cause-candidates")
    @BasicAuth
    public Map<String, Object> getRootCauseCandidates(@RequestBody Map<String, Object> request) {
        return super.getRootCauseCandidates(request);
    }

    @Override
    @PostMapping("/diagnosis/context")
    @BasicAuth
    public Map<String, Object> getDiagnosisContext(@RequestBody Map<String, Object> request) {
        return super.getDiagnosisContext(request);
    }

    @Override
    @PostMapping("/admin/export")
    @BasicAuth
    public Map<String, Object> exportGraph(@RequestBody Map<String, Object> request) {
        return super.exportGraph(request);
    }
}
