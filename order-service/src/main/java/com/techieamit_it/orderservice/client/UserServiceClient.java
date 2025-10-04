package com.techieamit_it.orderservice.client;

 import com.techieamit_it.orderservice.dto.UserDTO;
 import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallback = UserServiceFallback.class)
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);

    @GetMapping("/api/users/username/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);
}