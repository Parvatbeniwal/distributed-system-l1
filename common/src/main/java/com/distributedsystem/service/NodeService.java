package com.distributedsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NodeService {

    private static final Logger logger = LoggerFactory.getLogger(NodeService.class);

    @Value("${node.id:unknown}")
    private String nodeId;

    @Value("${peer.url:http://localhost:5001}")
    private String peerUrl;

    private final Map<String, String> store = new ConcurrentHashMap<>();

    private volatile long artificialDelayMs = 0L;

    private volatile boolean dropReplication = false;

    public void setArtificialDelayMs(long ms) { this.artificialDelayMs = ms; }

    public void setDropReplication(boolean drop) { this.dropReplication = drop; }

    private void maybeDelay() {
        if (artificialDelayMs > 0) {
            try { Thread.sleep(artificialDelayMs); } catch (InterruptedException ignored) {}
        }
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getHealth() {
        return "OK - " + nodeId;
    }

    public String get(String key) {
        return store.getOrDefault(key, "null");
    }

    public String put(String key, String value) {
        store.put(key, value);
        replicateToPeer(key, value);
        return "Stored on " + nodeId + " : " + key + " -> " + value;
    }

    public String replicate(String key, String value) {
        store.put(key, value);
        return "Replicated on " + nodeId + " : " + key + " -> " + value;
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("nodeId", nodeId);
        status.put("storedKeys", store.keySet());
        status.put("peerUrl", peerUrl);

        // Try to ping peer
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(peerUrl + "/health"))
                    .GET().timeout(Duration.ofSeconds(2))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            status.put("peerStatus", response.body());
        } catch (Exception e) {
            status.put("peerStatus", "DOWN: " + e.getMessage());
        }

        return status;
    }


    private void replicateToPeer(String key, String value) {
        new Thread(() -> {
            try {
                if (dropReplication) {
                    logger.warn("Replication dropped (fault injected) key={}", key);
                    return;
                }
                maybeDelay();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(peerUrl + "/replicate?key=" + key + "&value=" + value))
                        .POST(HttpRequest.BodyPublishers.noBody()).timeout(Duration.ofSeconds(2))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());
                logger.info("Replication successful: {} -> {} to peer {}", key, value, peerUrl);
            } catch (Exception e) {
                logger.error("Peer unreachable, will sync later: {}", e.getMessage());
            }
        }).start();
    }
}
