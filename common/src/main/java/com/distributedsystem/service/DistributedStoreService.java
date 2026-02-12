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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DistributedStoreService {

    private static final Logger logger = LoggerFactory.getLogger(DistributedStoreService.class);

    private final Map<String, String> store = new ConcurrentHashMap<>();

    @Value("${node.id:unknown}")
    private String nodeId;

    @Value("${peer.url:http://localhost:5032}")
    private String peerUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    // Write + replicate to follower
    public String putWithReplication(String key, String value) {
        store.put(key, value);
        logger.info("[{}] Stored k" +
                "ey={} value={}", nodeId, key, value);

        // replicate to follower synchronously (Strong Consistency)
        try {
            List<String> peerUrls = Arrays.asList(peerUrl.split(","));

            for (String peerUrl : peerUrls) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(peerUrl + "/replicate?key=" + key + "&value=" + value))
                        .timeout(Duration.ofSeconds(2))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                logger.info("[{}] Replication ACK from follower: {}", nodeId, response.body());
            }
            return "Stored & replicated: " + key + " -> " + value;

        } catch (Exception e) {
            logger.error("[{}] Replication failed: {}", nodeId, e.getMessage());
            return "Write failed, follower unreachable!";
        }
    }

    // Local replication endpoint (for follower)
    public String replicate(String key, String value) {
        store.put(key, value);
        logger.info("[{}] Replicated locally key={} value={}", nodeId, key, value);
        return "Replicated " + key + "=" + value;
    }

    // Get value
    public String get(String key) {
        return store.getOrDefault(key, "null");
    }
}

