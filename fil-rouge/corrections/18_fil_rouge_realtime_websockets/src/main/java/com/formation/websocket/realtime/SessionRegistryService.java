package com.formation.websocket.realtime;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class SessionRegistryService {

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    public void register(String sessionId, String username) {
        sessions.put(sessionId, new SessionInfo(sessionId, username, LocalDateTime.now()));
    }

    public SessionInfo unregister(String sessionId) {
        return sessions.remove(sessionId);
    }

    public int countConnectedUsers() {
        return sessions.size();
    }

    public Collection<SessionInfo> findAll() {
        return sessions.values();
    }
}
