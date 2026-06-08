/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.base;

import com.huawei.opsfactory.gateway.exception.ConflictException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;
import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link BaseBusinessServiceController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class BaseBusinessServiceControllerTest {

    @Mock
    private BusinessServiceService businessServiceService;

    @Mock
    private HttpServletRequest request;

    private BaseBusinessServiceController controller;

    @BeforeEach
    void setUp() {
        controller = new TestBusinessServiceController(businessServiceService);
    }

    /**
     * Test implementation of BaseBusinessServiceController for testing.
     */
    static class TestBusinessServiceController extends BaseBusinessServiceController {
        public TestBusinessServiceController(BusinessServiceService businessServiceService) {
            super(businessServiceService);
        }
    }

    /**
     * Test listing business services without filters.
     */
    @Test
    void testListBusinessServicesNoFilters() {
        List<Map<String, Object>> expectedServices = List.of(
            Map.of("id", "1", "name", "Service1"),
            Map.of("id", "2", "name", "Service2")
        );
        when(businessServiceService.listBusinessServices(null, null)).thenReturn(expectedServices);

        Map<String, Object> result = controller.listBusinessServices(null, null, null, request);

        assertTrue(result.containsKey("businessServices"));
        assertEquals(expectedServices, result.get("businessServices"));
        verify(businessServiceService).listBusinessServices(null, null);
    }

    /**
     * Test listing business services with keyword filter.
     */
    @Test
    void testListBusinessServicesWithKeyword() {
        String keyword = "test";
        List<Map<String, Object>> expectedServices = List.of(Map.of("id", "1", "name", "TestService"));
        when(businessServiceService.searchByKeyword(keyword)).thenReturn(expectedServices);

        Map<String, Object> result = controller.listBusinessServices(null, null, keyword, request);

        assertTrue(result.containsKey("businessServices"));
        assertEquals(expectedServices, result.get("businessServices"));
        verify(businessServiceService).searchByKeyword(keyword);
        verify(businessServiceService, never()).listBusinessServices(any(), any());
    }

    /**
     * Test getting a business service by ID successfully.
     */
    @Test
    void testGetBusinessServiceSuccess() throws NotFoundException {
        String id = "test-id";
        Map<String, Object> expectedService = Map.of("id", id, "name", "TestService");
        when(businessServiceService.getBusinessService(id)).thenReturn(expectedService);

        ResponseEntity<Map<String, Object>> response = controller.getBusinessService(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("success"));
        assertTrue(response.getBody().containsKey("businessService"));
        assertEquals(expectedService, response.getBody().get("businessService"));
    }

    /**
     * Test getting a non-existent business service throws NotFoundException.
     */
    @Test
    void testGetBusinessServiceNotFound() throws NotFoundException {
        String id = "non-existent";
        when(businessServiceService.getBusinessService(id)).thenThrow(new NotFoundException("Not found"));

        assertThrows(NotFoundException.class, () -> controller.getBusinessService(id, request));
    }

    /**
     * Test getting resolved business service successfully.
     */
    @Test
    void testGetResolvedSuccess() throws NotFoundException {
        String id = "test-id";
        Map<String, Object> expectedService = Map.of("id", id, "name", "TestService", "hosts", List.of());
        when(businessServiceService.getWithResolvedHosts(id)).thenReturn(expectedService);

        ResponseEntity<Map<String, Object>> response = controller.getResolved(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("success"));
        assertTrue(response.getBody().containsKey("businessService"));
    }

    /**
     * Test getting hosts for a business service successfully.
     */
    @Test
    void testGetHostsSuccess() throws NotFoundException {
        String id = "test-id";
        List<Map<String, Object>> expectedHosts = List.of(Map.of("id", "h1", "name", "Host1"));
        when(businessServiceService.getHostsForBusinessService(id)).thenReturn(expectedHosts);

        Map<String, Object> result = controller.getHosts(id, request);

        assertTrue(result.containsKey("hosts"));
        assertEquals(expectedHosts, result.get("hosts"));
    }

    /**
     * Test creating a business service successfully.
     */
    @Test
    void testCreateBusinessServiceSuccess() throws ConflictException, NotFoundException {
        Map<String, Object> requestBody = Map.of("name", "NewService", "description", "Test");
        Map<String, Object> createdService = Map.of("id", "new-id", "name", "NewService");
        when(businessServiceService.createBusinessService(requestBody)).thenReturn(createdService);

        ResponseEntity<Map<String, Object>> response = controller.createBusinessService(requestBody, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().containsKey("success"));
        assertTrue(response.getBody().containsKey("businessService"));
    }

    /**
     * Test creating a business service with conflict throws ConflictException.
     */
    @Test
    void testCreateBusinessServiceConflict() throws ConflictException, NotFoundException {
        Map<String, Object> requestBody = Map.of("name", "ExistingService");
        when(businessServiceService.createBusinessService(requestBody))
            .thenThrow(new ConflictException("Service already exists"));

        assertThrows(ConflictException.class, () -> controller.createBusinessService(requestBody, request));
    }

    /**
     * Test updating a business service successfully.
     */
    @Test
    void testUpdateBusinessServiceSuccess() throws NotFoundException, ConflictException {
        String id = "test-id";
        Map<String, Object> requestBody = Map.of("name", "UpdatedService");
        Map<String, Object> updatedService = Map.of("id", id, "name", "UpdatedService");
        when(businessServiceService.updateBusinessService(id, requestBody)).thenReturn(updatedService);

        ResponseEntity<Map<String, Object>> response = controller.updateBusinessService(id, requestBody, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("success"));
        assertTrue(response.getBody().containsKey("businessService"));
    }

    /**
     * Test updating a non-existent business service throws NotFoundException.
     */
    @Test
    void testUpdateBusinessServiceNotFound() throws NotFoundException, ConflictException {
        String id = "non-existent";
        Map<String, Object> requestBody = Map.of("name", "Updated");
        when(businessServiceService.updateBusinessService(id, requestBody))
            .thenThrow(new NotFoundException("Not found"));

        assertThrows(NotFoundException.class, () -> controller.updateBusinessService(id, requestBody, request));
    }

    /**
     * Test deleting a business service successfully.
     */
    @Test
    void testDeleteBusinessServiceSuccess() {
        String id = "test-id";
        when(businessServiceService.deleteBusinessService(id)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = controller.deleteBusinessService(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("success"));
        assertEquals(true, response.getBody().get("success"));
    }

    /**
     * Test deleting a non-existent business service returns 404.
     */
    @Test
    void testDeleteBusinessServiceNotFound() {
        String id = "non-existent";
        when(businessServiceService.deleteBusinessService(id)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = controller.deleteBusinessService(id, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("success"));
        assertEquals(false, response.getBody().get("success"));
        assertTrue(response.getBody().containsKey("error"));
    }

    /**
     * Test getting topology for a business service successfully.
     */
    @Test
    void testGetTopologySuccess() throws NotFoundException {
        String id = "test-id";
        Map<String, Object> expectedTopology = Map.of("nodes", List.of(), "edges", List.of());
        when(businessServiceService.getTopologyForBusinessService(id)).thenReturn(expectedTopology);

        Map<String, Object> result = controller.getTopology(id, request);

        assertEquals(expectedTopology, result);
    }

    /**
     * Test migration endpoint.
     */
    @Test
    void testMigrate() {
        Map<String, Object> expectedResult = Map.of("migrated", 10, "failed", 0);
        when(businessServiceService.migrateFromBusinessField()).thenReturn(expectedResult);

        Map<String, Object> result = controller.migrate(request);

        assertEquals(expectedResult, result);
        verify(businessServiceService).migrateFromBusinessField();
    }
}
