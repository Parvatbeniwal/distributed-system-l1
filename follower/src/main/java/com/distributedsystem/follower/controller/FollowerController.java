package com.distributedsystem.follower.controller;

import com.distributedsystem.service.DistributedStoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FollowerController {

    private final DistributedStoreService storeService;

    public FollowerController(DistributedStoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/replicate")
    public String replicate(@RequestParam String key, @RequestParam String value) {
        return storeService.replicate(key, value);
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return storeService.get(key);
    }
}
