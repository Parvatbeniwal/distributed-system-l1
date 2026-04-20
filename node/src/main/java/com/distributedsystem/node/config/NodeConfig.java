package com.distributedsystem.node.config;

import com.distributedsystem.common.node.NodeState;
import com.distributedsystem.common.service.DistributedStoreService;
import com.distributedsystem.common.service.ReplicationService;
import com.distributedsystem.node.service.ElectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.distributedsystem.common.node.NodeRole.*;

@Configuration
public class NodeConfig {
    @Bean
    public NodeState nodeState() {
        NodeState nodeState = new NodeState();
        nodeState.updateHeartbeatTime();
        return nodeState;
    }

    @Bean
    public DistributedStoreService storeService(NodeState nodeState, ReplicationService replicationService, @Value("${node.id}") String nodeId, @Value("${peer.urls:}") String peerUrls) {
        return new DistributedStoreService(nodeState, replicationService, nodeId, peerUrls);
    }

    @Bean
    public CommandLineRunner initLeader(NodeState nodeState, @Value("${node.id}") String nodeId) {
        return args -> {
            if ("A".equals(nodeId)) {
                nodeState.setRole(LEADER);
                nodeState.setCurrentLeader(nodeId);
            } else {
                nodeState.setRole(FOLLOWER);
                nodeState.setNodeId(nodeId);
            }
        };
    }


}
