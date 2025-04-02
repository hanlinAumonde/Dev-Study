package com.devStudy.oauth2.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/Test1")
    public String getTest1() {
        return " hello Message 1";
    }

    @GetMapping("/Test2")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String getTest2() {
        return " hello Message 2";
    }

    @GetMapping("/Test3")
    @PreAuthorize("hasAuthority('SCOPE_Message')")
    public String getTest3() {
        return " hello Message 3";
    }

}
