package com.distributedsystem.common.service;

import com.distributedsystem.common.model.AppendEntriesResponse;
import com.distributedsystem.common.node.LogEntry;
import com.distributedsystem.common.node.NodeRole;
import com.distributedsystem.common.node.NodeState;
import org.gradle.internal.impldep.com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ReplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ReplicationService.class);

    private final HttpClient client = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public int appendEntriesToPeers(List<String> peers, LogEntry entry) {
        String json = GSON.toJson(entry);
        int successCount = 0;
        for (String peer : peers) {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(peer + "/node/append"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                logger.info("Replicated to peer {} status={}", peer, response.statusCode());
                if (response.statusCode() == 200) {
                    final AppendEntriesResponse appendEntriesResponse = GSON.fromJson(response.body(), AppendEntriesResponse.class);
                    if (appendEntriesResponse.isSuccess()){
                        successCount++;
                    }
                }
            } catch (Exception e) {
                logger.warn("Replication failed to {} due to {}", peer, e.toString());
            }
        }
        logger.info("Replicated success count {} ", successCount);
        return successCount;
    }


     /*
    public void replicateToPeers(List<String> peers, String key, String value) {
        for (String peer : peers) {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(peer + "/node/append?key=" + key + "&value=" + value))
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                logger.info("Replicated to peer {}", peer);

            } catch (Exception e) {
                logger.warn("Replication failed to {}", peer);
            }
        }
    }*/
}
