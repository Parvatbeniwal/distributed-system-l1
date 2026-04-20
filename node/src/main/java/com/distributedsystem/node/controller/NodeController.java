package com.distributedsystem.node.controller;

import com.distributedsystem.common.model.AppendEntriesRequest;
import com.distributedsystem.common.model.AppendEntriesResponse;
import com.distributedsystem.common.model.VoteRequest;
import com.distributedsystem.common.model.VoteResponse;
import com.distributedsystem.common.node.LogEntry;
import com.distributedsystem.common.node.NodeRole;
import com.distributedsystem.common.node.NodeState;
import com.distributedsystem.common.service.DistributedStoreService;
import org.springframework.web.bind.annotation.*;

import static com.distributedsystem.common.node.NodeRole.FOLLOWER;

@RestController
@RequestMapping("/node")
public class NodeController {

    private final DistributedStoreService storeService;
    private final NodeState nodeState;

    public NodeController(DistributedStoreService storeService, NodeState nodeState) {
        this.storeService = storeService;
        this.nodeState = nodeState;
    }

    @PostMapping("/put")
    public String put(@RequestParam String key, @RequestParam String value) {
        return storeService.put(key, value);
    }

    @PostMapping("/replicate")
    public String replicate(@RequestParam String key, @RequestParam String value) {
        return storeService.replicate(key, value);
    }

    @PostMapping("/append")
    public synchronized AppendEntriesResponse append(@RequestBody AppendEntriesRequest request) {
        if (request.getTerm() < nodeState.getTerm()) {
            return new AppendEntriesResponse(nodeState.getTerm(), false);
        }
        nodeState.setRole(NodeRole.FOLLOWER);
        nodeState.setCurrentLeader(request.getLeaderId());
        nodeState.updateHeartbeatTime();

        // Validate previous log index
        if (request.getPrevLogIndex() > nodeState.getLog().size()) {
            return new AppendEntriesResponse(nodeState.getTerm(), false);
        }

        // Append new entries
        for (LogEntry entry : request.getEntries()) {
            nodeState.appendEntry(entry);
        }

        // Update commit index
        if (request.getLeaderCommit() > nodeState.getCommitIndex()) {
            nodeState.setCommitIndex(Math.min(request.getLeaderCommit(), nodeState.getLog().size())
            );
        }

        return new AppendEntriesResponse(nodeState.getTerm(), true);
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return storeService.get(key);
    }

    @GetMapping("/role")
    public String role() {
        return nodeState.getRole().name();
    }

    @PostMapping("/heartbeat")
    public String heartbeat(@RequestParam String leaderId, @RequestParam long term) {
        if (term >= nodeState.getTerm()) {
            nodeState.setRole(NodeRole.FOLLOWER);
            nodeState.setCurrentLeader(leaderId);
            nodeState.updateHeartbeatTime();
        }
        return "Heartbeat received";
    }

    @PostMapping("/vote")
    public synchronized VoteResponse vote(@RequestBody VoteRequest request) {
        long requestTerm = request.getTerm();
        long currentTerm = nodeState.getTerm();
        // Reject stale term
        if (requestTerm < currentTerm) {
            return new VoteResponse(false, currentTerm);
        }
        // If request term is higher → step down
        if (requestTerm > currentTerm) {
            nodeState.incrementTerm();
            nodeState.setRole(NodeRole.FOLLOWER);
            nodeState.setVotedFor(null);
        }

        // Already voted for someone else this term
        if (nodeState.getVotedFor() != null &&
                !nodeState.getVotedFor().equals(request.getCandidateId())) {

            return new VoteResponse(false, nodeState.getTerm());
        }

        long myLastLogIndex = nodeState.getLog().size();
        long myLastLogTerm = myLastLogIndex == 0 ? 0 : nodeState.getLog().get((int) myLastLogIndex - 1).getTerm();

        boolean candidateUpToDate = request.getLastLogTerm() > myLastLogTerm ||
                (request.getLastLogTerm() == myLastLogTerm && request.getLastLogIndex() >= myLastLogIndex);

        if (!candidateUpToDate) {
            return new VoteResponse(false, nodeState.getTerm());
        }

        // Grant vote
        nodeState.setVotedFor(request.getCandidateId());
        nodeState.updateHeartbeatTime();
        System.out.println("Voted for " + request.getCandidateId());
        return new VoteResponse(true, nodeState.getTerm());
    }

}
