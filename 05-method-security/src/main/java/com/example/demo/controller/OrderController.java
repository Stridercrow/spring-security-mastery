package com.example.demo.controller;
import com.example.demo.service.OrderService; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/orders")
public class OrderController {
 private final OrderService service; public OrderController(OrderService service){this.service=service;}
 @GetMapping("/{id}") String get(@PathVariable long id){return service.get(id);}
 @DeleteMapping("/{id}") String delete(@PathVariable long id){return service.cancel(id);}
}
