package com.distributedsystem.common.node;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class NodeState {
    private volatile NodeRole role = NodeRole.FOLLOWER;
    private final AtomicLong currentTerm = new AtomicLong(0);
    private volatile String votedFor = null;
    private volatile String nodeId = null;
    private volatile String currentLeader = null;
    private volatile long lastHeartbeatTime = System.currentTimeMillis();

    private final List<LogEntry> log = new CopyOnWriteArrayList<>();
    private volatile long commitIndex = 0;
    private volatile long lastApplied = 0;

    public NodeRole getRole() {
        return role;
    }

    public void setRole(NodeRole role) {
        this.role = role;
    }

    public long getTerm() {
        return currentTerm.get();
    }

    public long incrementTerm() {
        votedFor = null; // reset vote on new term
        return currentTerm.incrementAndGet();
    }

    public void updateHeartbeatTime() {
        lastHeartbeatTime = System.currentTimeMillis();
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public String getCurrentLeader() {
        return currentLeader;
    }

    public void setCurrentLeader(String currentLeader) {
        this.currentLeader = currentLeader;
    }

    public String getVotedFor() {
        return votedFor;
    }

    public void setVotedFor(String votedFor) {
        this.votedFor = votedFor;
    }

    public synchronized long appendEntry(LogEntry entry) {
        log.add(entry);
        return entry.getIndex();
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public AtomicLong getCurrentTerm() {
        return currentTerm;
    }

    public void setLastHeartbeatTime(long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public List<LogEntry> getLog() {
        return log;
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    public void setCommitIndex(long commitIndex) {
        this.commitIndex = commitIndex;
    }

    public long getLastApplied() {
        return lastApplied;
    }

    public void setLastApplied(long lastApplied) {
        this.lastApplied = lastApplied;
    }

    public long incrementLastApplied() {
        return ++lastApplied  ;
    }
}
