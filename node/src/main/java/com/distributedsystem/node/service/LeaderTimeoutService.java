package com.distributedsystem.node.service;

import com.distributedsystem.common.node.NodeRole;
import com.distributedsystem.common.node.NodeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class LeaderTimeoutService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderTimeoutService.class);
    private final NodeState nodeState;
    private final ElectionService electionService;
    private long electionTimeout = ThreadLocalRandom.current().nextLong(4000, 8000);


    @Autowired
    public LeaderTimeoutService(NodeState nodeState, ElectionService electionService) {
        this.nodeState = nodeState;
        this.electionService = electionService;
    }

    @Scheduled(fixedRate = 1000)
    public void detectLeaderFailure() {
        if (nodeState.getRole() == NodeRole.LEADER) {
            return;
        }
        long now = System.currentTimeMillis();
        long lastHeartbeat = nodeState.getLastHeartbeatTime();
        boolean timeoutDetected = now - lastHeartbeat > electionTimeout;
        if (timeoutDetected) {
            electionTimeout = ThreadLocalRandom.current().nextLong(4000, 8000);
            electionService.startElection();
        }
    }
}
