/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.controlcenter.api;

import com.huawei.opsfactory.controlcenter.events.EventStoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.apache.servicecomb.provider.rest.common.RestSchema;

/**
 * Event Controller.
 *
 * @author x00000000
 * @since 2026-05-27
 */
@RestController
@RestSchema(schemaId = "eventController")
@RequestMapping("/api/control-center/events")
public class EventController {

    private final EventStoreService eventStoreService;

    /**
     * Creates the event controller instance.
     *
     * @param eventStoreService the event store service
     */
    public EventController(EventStoreService eventStoreService) {
        this.eventStoreService = eventStoreService;
    }

    @GetMapping
    public Map<String, Object> list() {
        return Map.of("events", eventStoreService.list());
    }
}
