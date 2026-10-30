package com.example.demo;
import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException; import org.springframework.security.test.context.support.WithMockUser;
import com.example.demo.service.OrderService;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest class MethodSecurityTest {
 @Autowired OrderService orders;
 @Test @WithMockUser(roles="USER") void userCannotCancel(){assertThrows(AccessDeniedException.class,()->orders.cancel(1));}
 @Test @WithMockUser(roles="ADMIN") void adminCanCancel(){orders.cancel(1);}
}
