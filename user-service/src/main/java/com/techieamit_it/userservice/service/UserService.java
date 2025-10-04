package com.techieamit_it.userservice.service;

import com.techieamit_it.userservice.dto.UserCreateDTO;
import com.techieamit_it.userservice.dto.UserDTO;
import com.techieamit_it.userservice.dto.UserUpdateDTO;
import com.techieamit_it.userservice.exception.UserAlreadyExistsException;
import com.techieamit_it.userservice.exception.UserNotFoundException;
import com.techieamit_it.userservice.model.User;
import com.techieamit_it.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        logger.info("Creating new user with username: {}", userCreateDTO.getUsername());

        // Check if user already exists
        if (userRepository.existsByUsernameOrEmail(userCreateDTO.getUsername(), userCreateDTO.getEmail())) {
            logger.warn("User already exists with username: {} or email: {}", userCreateDTO.getUsername(), userCreateDTO.getEmail());
            throw new UserAlreadyExistsException(userCreateDTO.getUsername(), userCreateDTO.getEmail());
        }

        // Create new user
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setEmail(userCreateDTO.getEmail());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPhoneNumber(userCreateDTO.getPhoneNumber());

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with id: {}", savedUser.getId());

        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(Long userId) {
        logger.info("Fetching user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", userId);
                    return new UserNotFoundException(userId);
                });

        return convertToDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        logger.info("Fetching user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UserNotFoundException(username, true);
                });

        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        logger.info("Fetching all users");

        List<User> users = userRepository.findAll();
        logger.info("Found {} users", users.size());

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<UserDTO> getUsers(Pageable pageable) {
        logger.info("Fetching users with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<User> userPage = userRepository.findAll(pageable);
        logger.info("Found {} users out of {} total", userPage.getNumberOfElements(), userPage.getTotalElements());

        return userPage.map(this::convertToDTO);
    }

    public UserDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        logger.info("Updating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", userId);
                    return new UserNotFoundException(userId);
                });

        // Update fields if provided
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getFirstName() != null) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null) {
            user.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }
        if (userUpdateDTO.getIsActive() != null) {
            user.setIsActive(userUpdateDTO.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with id: {}", updatedUser.getId());

        return convertToDTO(updatedUser);
    }

    public void deleteUser(Long userId) {
        logger.info("Deleting user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", userId);
                    return new UserNotFoundException(userId);
                });

        userRepository.delete(user);
        logger.info("User deleted successfully with id: {}", userId);
    }

    public void deactivateUser(Long userId) {
        logger.info("Deactivating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", userId);
                    return new UserNotFoundException(userId);
                });

        user.setIsActive(false);
        userRepository.save(user);
        logger.info("User deactivated successfully with id: {}", userId);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setIsActive(user.getIsActive());
        return userDTO;
    }
}