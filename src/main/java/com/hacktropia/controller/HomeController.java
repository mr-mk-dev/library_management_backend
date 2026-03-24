package com.hacktropia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Home", description = "Health check and welcome endpoints — no authentication required")
public class HomeController {

    @GetMapping
    @Operation(summary = "Welcome message", description = "Returns a welcome message for the Library Management System")
    public ResponseEntity<String> localhostCheckpoint() {
        return ResponseEntity.ok("welcome to library management system");
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns server running status")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Library management server is running...");
    }
}
