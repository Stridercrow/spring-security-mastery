package com.example.demo.controller;
import org.springframework.web.bind.annotation.*;
@RestController public class ApiController {
 @GetMapping("/public") Object pub(){return java.util.Map.of("message","public");}
 @GetMapping("/api/read") Object read(){return java.util.Map.of("message","read allowed");}
 @GetMapping("/api/write") Object write(){return java.util.Map.of("message","write allowed");}
}
