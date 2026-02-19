package com.distributedsystem.node.service;

import com.distributedsystem.common.node.NodeRole;
import com.distributedsystem.common.node.NodeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

@Service
public class HeartBeatService {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatService.class);

    private final NodeState nodeState;

    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${node.id}")
    private String nodeId;

    @Value("${peer.urls:}")
    private String peerUrls;

    public HeartBeatService(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    @Scheduled(fixedRate = 2000)
    public void sendHeartbeat() {
        if (nodeState.getRole() != NodeRole.LEADER) {
            return;
        }
        final List<String> peers = Arrays.asList(peerUrls.split(","));
        for (String peer : peers) {
            if (peer.isBlank()) {
                continue;
            }

            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(peer
                                        + "/node/heartbeat?leaderId=" + nodeId)).POST(HttpRequest.BodyPublishers.noBody()).build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                logger.info("Heartbeat sent from {} to {}", nodeId, peer);

            } catch (Exception e) {
                logger.warn("Heartbeat failed to {}", peer);
            }
        }
    }
}
