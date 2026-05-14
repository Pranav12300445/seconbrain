package com.pranav.second_brain_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/hello")
    public String userHello() {
        return "Hello User";
    }
}