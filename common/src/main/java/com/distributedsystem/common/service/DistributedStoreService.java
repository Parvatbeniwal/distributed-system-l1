package com.distributedsystem.common.service;

import com.distributedsystem.common.node.NodeRole;
import com.distributedsystem.common.node.NodeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedStoreService {

    private static final Logger logger = LoggerFactory.getLogger(DistributedStoreService.class);
    private final Map<String, String> store = new ConcurrentHashMap<>();

    private final NodeState nodeState;
    private final ReplicationService replicationService;
    private final String nodeId;
    private final List<String> peers;

    public DistributedStoreService(NodeState nodeState, ReplicationService replicationService, String nodeId, String peerUrls) {

        this.nodeState = nodeState;
        this.replicationService = replicationService;
        this.nodeId = nodeId;
        this.peers = Arrays.stream(peerUrls.split(",")).filter(url -> !url.isBlank()).toList();
        logger.info("Node {} initialized with peers {}",
                nodeId, peers);
    }

    /**
     * Client write entry point
     * Only leader can accept writes
     */
    public String put(String key, String value) {
        if (nodeState.getRole() != NodeRole.LEADER) {
            String leader = nodeState.getCurrentLeader();
            logger.warn("Write rejected. Node {} is not leader. Leader is {}", nodeId, leader);
            return "Not leader. Current leader is: " + leader;
        }
        // Store locally first
        store.put(key, value);
        logger.info("[{}] Stored locally key={} value={}", nodeId, key, value);
        // Replicate to followers
        replicateToFollowers(key, value);
        return "Stored and replicated by leader " + nodeId;
    }

    /**
     * Called by leader to replicate to followers
     */
    private void replicateToFollowers(String key, String value) {
        replicationService.replicateToPeers(peers, key, value
        );
    }

    /**
     * Called by followers to accept replication
     */
    public String replicate(String key, String value) {
        store.put(key, value);
        logger.info("[{}] Replicated from leader key={} value={}", nodeId, key, value);
        return "Replicated on follower " + nodeId;
    }

    /**
     * Read operation
     * Any node can serve reads
     */
    public String get(String key) {
        String value = store.getOrDefault(key, "null");
        logger.info("[{}] Read key={} value={}", nodeId, key, value);
        return value;
    }

}
