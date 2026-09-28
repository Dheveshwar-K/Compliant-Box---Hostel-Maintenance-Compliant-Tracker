package com.example.complaintbox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/complaints")
    public String complaints() {
        return "complaints";
    }

    @GetMapping("/residents")
    public String residents() {
        return "residents";
    }

    @GetMapping("/staff")
    public String staff() {
        return "staff";
    }

    @GetMapping("/categories")
    public String categories() {
        return "categories";
    }
}