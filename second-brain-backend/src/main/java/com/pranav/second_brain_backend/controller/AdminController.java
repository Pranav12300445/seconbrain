package com.pranav.second_brain_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/hello")
    public String adminHello() {
        return "Hello Admin";
    }
}