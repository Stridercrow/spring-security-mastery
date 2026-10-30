package com.example.demo.controller;
import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/admin") public class AdminController {
 @GetMapping("/status") @PreAuthorize("hasRole('ADMIN')") Object status(){return java.util.Map.of("status","ok");}
}
