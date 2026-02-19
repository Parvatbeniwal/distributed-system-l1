package com.distributedsystem.common.service;

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

    public void replicateToPeers(List<String> peers, String key, String value) {

        for (String peer : peers) {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(peer + "/node/replicate?key=" + key + "&value=" + value))
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                logger.info("Replicated to peer {}", peer);

            } catch (Exception e) {
                logger.warn("Replication failed to {}", peer);
            }
        }
    }

}
