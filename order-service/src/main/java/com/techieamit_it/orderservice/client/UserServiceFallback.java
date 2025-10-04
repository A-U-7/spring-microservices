package com.techieamit_it.orderservice.client;


import com.techieamit_it.orderservice.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallback implements UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceFallback.class);

    @Override
    public UserDTO getUserById(Long userId) {
        logger.error("User service is down. Fallback for getUserById({})", userId);
        UserDTO fallbackUser = new UserDTO();
        fallbackUser.setId(userId);
        fallbackUser.setUsername("Unknown User");
        fallbackUser.setEmail("unknown@example.com");
        fallbackUser.setFirstName("Unknown");
        fallbackUser.setLastName("User");
        fallbackUser.setIsActive(false);
        return fallbackUser;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        logger.error("User service is down. Fallback for getUserByUsername({})", username);
        UserDTO fallbackUser = new UserDTO();
        fallbackUser.setId(-1L);
        fallbackUser.setUsername(username);
        fallbackUser.setEmail("unknown@example.com");
        fallbackUser.setFirstName("Unknown");
        fallbackUser.setLastName("User");
        fallbackUser.setIsActive(false);
        return fallbackUser;
    }
}