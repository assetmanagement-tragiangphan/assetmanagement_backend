package com.nashtech.rookies.assetmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/hello")
    public String hello() {
        System.out.println(passwordEncoder.encode("123"));

        return "Hello, World!";
    }
}
