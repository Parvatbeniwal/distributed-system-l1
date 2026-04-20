package com.distributedsystem.common.model;

public class VoteRequest {

    private String candidateId;
    private long term;
    private long lastLogIndex;
    private long lastLogTerm;

    public VoteRequest() {}

    public VoteRequest(String candidateId, long term) {
        this.candidateId = candidateId;
        this.term = term;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public long getTerm() {
        return term;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public long getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(long lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }
}
