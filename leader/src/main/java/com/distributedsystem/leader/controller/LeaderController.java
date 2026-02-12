package com.distributedsystem.leader.controller;

import com.distributedsystem.service.DistributedStoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderController {

    private final DistributedStoreService storeService;

    public LeaderController(DistributedStoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/put")
    public String put(@RequestParam String key, @RequestParam String value) {
        return storeService.putWithReplication(key, value);
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return storeService.get(key);
    }
}
