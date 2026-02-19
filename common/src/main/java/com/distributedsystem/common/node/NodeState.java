package com.distributedsystem.common.node;

import java.util.concurrent.atomic.AtomicLong;

public class NodeState {

    private volatile NodeRole role = NodeRole.FOLLOWER;
    private volatile String currentLeader = null;
    private volatile long lastHeartbeatTime = System.currentTimeMillis();

    private final AtomicLong term = new AtomicLong(0);

    public NodeRole getRole() {
        return role;
    }

    public void setRole(NodeRole role) {
        this.role = role;
    }

    public String getCurrentLeader() {
        return currentLeader;
    }

    public void setCurrentLeader(String currentLeader) {
        this.currentLeader = currentLeader;
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void updateHeartbeatTime() {
        this.lastHeartbeatTime = System.currentTimeMillis();
    }

    public long incrementTerm() {
        return term.incrementAndGet();
    }

    public long getTerm() {
        return term.get();
    }

}
