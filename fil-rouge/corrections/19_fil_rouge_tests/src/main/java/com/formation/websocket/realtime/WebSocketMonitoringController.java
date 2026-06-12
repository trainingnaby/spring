package com.formation.websocket.realtime;

import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ws-monitoring")
public class WebSocketMonitoringController {

    private final WebSocketMonitoringService monitoringService;
    private final SessionRegistryService sessionRegistryService;

    public WebSocketMonitoringController(WebSocketMonitoringService monitoringService,
            SessionRegistryService sessionRegistryService) {
        this.monitoringService = monitoringService;
        this.sessionRegistryService = sessionRegistryService;
    }

    @GetMapping("/stats")
    public DashboardStats stats() {
        return monitoringService.currentStats();
    }

    @GetMapping("/sessions")
    public Collection<SessionInfo> sessions() {
        return sessionRegistryService.findAll();
    }
}
