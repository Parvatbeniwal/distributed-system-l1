package com.distributedsystem.controller;

import com.distributedsystem.service.NodeService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/node")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from " + nodeService.getNodeId();
    }

    @GetMapping("/health")
    public String health() {
        return nodeService.getHealth();
    }

    @PostMapping("/put")
    public String put(@RequestParam String key, @RequestParam String value) {
        return nodeService.put(key, value);
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return nodeService.get(key);
    }

    @PostMapping("/replicate")
    public String replicate(@RequestParam String key, @RequestParam String value) {
        return nodeService.replicate(key, value);
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return nodeService.getStatus();
    }

    //curl -X POST "http://localhost:5031/admin/delay?ms=3000"
    // Inject faults: simulate latency
    @PostMapping("/admin/delay")
    public String setDelay(@RequestParam long ms) {
        nodeService.setArtificialDelayMs(ms);
        return "Artificial delay set to " + ms + " ms";
    }

    // curl -X POST "http://localhost:5031/admin/dropReplication?drop=true"
    // Inject faults: simulate partition (drop replication)
    @PostMapping("/admin/dropReplication")
    public String setDropReplication(@RequestParam boolean drop) {
        nodeService.setDropReplication(drop);
        return "Drop replication set to " + drop;
    }
}
