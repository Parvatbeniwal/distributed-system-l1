package com.distributedsystem.node.controller;

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

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return storeService.get(key);
    }

    @GetMapping("/role")
    public String role() {
        return nodeState.getRole().name();
    }

    @PostMapping("/heartbeat")
    public String heartbeat(@RequestParam String leaderId) {
        nodeState.setCurrentLeader(leaderId);
        nodeState.updateHeartbeatTime();
        nodeState.setRole(FOLLOWER);
        return "Heartbeat received from " + leaderId;
    }
}
