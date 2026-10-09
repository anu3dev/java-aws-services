package com.example.java_aws_services.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {
    @GetMapping
    public String getHelloDefault() {
        return "Hey Guest!";
    }

    @GetMapping("/{name}")
    public String getHelloWithName(@PathVariable String name) {
        return "Hey " + name.substring(0, 1).toUpperCase() + name.substring(1) + "!";
    }
}