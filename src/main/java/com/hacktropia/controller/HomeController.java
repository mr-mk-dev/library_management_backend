package com.hacktropia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {

    @GetMapping
    public ResponseEntity<String> localhostCheckpoint() {
        return ResponseEntity.ok("welcome to library management system");
    }
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(){
        return  ResponseEntity.ok("Library management server is running...");
    }
}
