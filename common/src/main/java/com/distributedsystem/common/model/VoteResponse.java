package com.distributedsystem.common.model;

public class VoteResponse {

    private boolean voteGranted;
    private long term;

    public VoteResponse() {}

    public VoteResponse(boolean voteGranted, long term) {
        this.voteGranted = voteGranted;
        this.term = term;
    }

    public boolean isVoteGranted() {
        return voteGranted;
    }

    public long getTerm() {
        return term;
    }
}
