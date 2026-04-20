package com.distributedsystem.node.service;

import com.distributedsystem.common.model.VoteRequest;
import com.distributedsystem.common.model.VoteResponse;
import com.distributedsystem.common.node.NodeRole;
import com.distributedsystem.common.node.NodeState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

@Service
public class ElectionService {

    private final NodeState nodeState;

    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${node.id}")
    private String nodeId;

    @Value("${peer.urls}")
    private String peerUrls;

    @Autowired
    public ElectionService(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    public void startElection() {
        nodeState.setRole(NodeRole.CANDIDATE);
        long term = nodeState.incrementTerm();
        int votes = 1;
        System.out.println(nodeId + " started election");
        final List<String> peers = Arrays.asList(peerUrls.split(","));
        for (String peer : peers) {
            try {
                final VoteRequest request = new VoteRequest(nodeId, term);
                final String json = new ObjectMapper().writeValueAsString(request);
                final HttpRequest httpRequest = HttpRequest.newBuilder()
                                .uri(URI.create(peer + "/node/vote"))
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .header("Content-Type", "application/json")
                                .build();

                final HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                final VoteResponse vote = new ObjectMapper().readValue(response.body(), VoteResponse.class);
                if (vote.getTerm() > nodeState.getTerm()) {
                    nodeState.setRole(NodeRole.FOLLOWER);
                    nodeState.setVotedFor(null);
                    return;
                }

                if (vote.isVoteGranted()) {
                    votes++;
                }

            } catch (Exception ignored) {
                //ignored
            }
        }

        if (votes > peers.size() / 2) {
            nodeState.setRole(NodeRole.LEADER);
            nodeState.setCurrentLeader(nodeId);
            nodeState.setVotedFor(null);
            System.out.println(nodeId + " became LEADER for term " + nodeState.getTerm());
        }
    }
}
