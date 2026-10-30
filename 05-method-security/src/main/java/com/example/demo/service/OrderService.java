package com.example.demo.service;
import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.stereotype.Service;
@Service public class OrderService {
 @PreAuthorize("hasRole('ADMIN')") public String cancel(long id){return "cancelled-"+id;}
 @PreAuthorize("hasAnyRole('USER','ADMIN')") public String get(long id){return "order-"+id;}
}
