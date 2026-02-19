package com.distributedsystem.node.service;

import com.distributedsystem.common.node.NodeRole;
import com.distributedsystem.common.node.NodeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LeaderTimeoutService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderTimeoutService.class);

    private final NodeState nodeState;

    public LeaderTimeoutService(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    @Scheduled(fixedRate = 3000)
    public void detectLeaderFailure() {
        long timeout = 5000;
        long lastHeartbeat = nodeState.getLastHeartbeatTime();
        long now = System.currentTimeMillis();

        if (nodeState.getRole() == NodeRole.LEADER) {
            return;
        }

        if (now - lastHeartbeat > timeout) {
            logger.warn("Leader timeout detected. Becoming candidate.");
            nodeState.setRole(NodeRole.CANDIDATE);
            nodeState.incrementTerm();
        }
    }
}
