package org.website.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description: TODO
 * @date 2026/6/9 08:45
 */
@RestController
public class Hello {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World!";
    }

}
