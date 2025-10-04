package com.techieamit_it.orderservice.service;

import com.techieamit_it.orderservice.client.UserServiceClient;
import com.techieamit_it.orderservice.dto.OrderCreateDTO;
import com.techieamit_it.orderservice.dto.OrderDTO;
import com.techieamit_it.orderservice.dto.OrderUpdateDTO;
import com.techieamit_it.orderservice.dto.UserDTO;
import com.techieamit_it.orderservice.exception.OrderNotFoundException;
import com.techieamit_it.orderservice.exception.UserNotFoundException;
import com.techieamit_it.orderservice.model.Order;
import com.techieamit_it.orderservice.model.OrderStatus;
import com.techieamit_it.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserServiceClient userServiceClient) {
        this.orderRepository = orderRepository;
        this.userServiceClient = userServiceClient;
    }

    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO) {
        logger.info("Creating new order for userId: {}", orderCreateDTO.getUserId());

        // Validate user exists
        try {
            UserDTO user = userServiceClient.getUserById(orderCreateDTO.getUserId());
            if (user == null || !user.getIsActive()) {
                logger.warn("User not found or inactive with id: {}", orderCreateDTO.getUserId());
                throw new UserNotFoundException(orderCreateDTO.getUserId());
            }
        } catch (Exception e) {
            logger.error("Error validating user: {}", e.getMessage());
            throw new UserNotFoundException(orderCreateDTO.getUserId());
        }

        // Create new order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(orderCreateDTO.getUserId());
        order.setProductName(orderCreateDTO.getProductName());
        order.setQuantity(orderCreateDTO.getQuantity());
        order.setPrice(orderCreateDTO.getPrice());
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with id: {} and order number: {}", 
                   savedOrder.getId(), savedOrder.getOrderNumber());

        return convertToDTO(savedOrder);
    }

    public OrderDTO getOrderById(Long orderId) {
        logger.info("Fetching order with id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with id: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });

        return convertToDTO(order);
    }

    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        logger.info("Fetching order with order number: {}", orderNumber);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> {
                    logger.warn("Order not found with order number: {}", orderNumber);
                    return new OrderNotFoundException(orderNumber, true);
                });

        return convertToDTO(order);
    }

    public List<OrderDTO> getAllOrders() {
        logger.info("Fetching all orders");

        List<Order> orders = orderRepository.findAll();
        logger.info("Found {} orders", orders.size());

        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<OrderDTO> getOrders(Pageable pageable) {
        logger.info("Fetching orders with pagination - page: {}, size: {}", 
                   pageable.getPageNumber(), pageable.getPageSize());

        Page<Order> orderPage = orderRepository.findAll(pageable);
        logger.info("Found {} orders out of {} total", 
                   orderPage.getNumberOfElements(), orderPage.getTotalElements());

        return orderPage.map(this::convertToDTO);
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        logger.info("Fetching orders for user with id: {}", userId);

        List<Order> orders = orderRepository.findByUserId(userId);
        logger.info("Found {} orders for user with id: {}", orders.size(), userId);

        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        logger.info("Fetching orders for user with id: {} with pagination", userId);

        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        logger.info("Found {} orders out of {} total for user with id: {}", 
                   orderPage.getNumberOfElements(), orderPage.getTotalElements(), userId);

        return orderPage.map(this::convertToDTO);
    }

    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        logger.info("Fetching orders with status: {}", status);

        List<Order> orders = orderRepository.findByStatus(status);
        logger.info("Found {} orders with status: {}", orders.size(), status);

        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        logger.info("Fetching orders with status: {} with pagination", status);

        Page<Order> orderPage = orderRepository.findByStatus(status, pageable);
        logger.info("Found {} orders out of {} total with status: {}", 
                   orderPage.getNumberOfElements(), orderPage.getTotalElements(), status);

        return orderPage.map(this::convertToDTO);
    }

    public OrderDTO updateOrder(Long orderId, OrderUpdateDTO orderUpdateDTO) {
        logger.info("Updating order with id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with id: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });

        // Update fields if provided
        if (orderUpdateDTO.getProductName() != null) {
            order.setProductName(orderUpdateDTO.getProductName());
        }
        if (orderUpdateDTO.getQuantity() != null) {
            order.setQuantity(orderUpdateDTO.getQuantity());
        }
        if (orderUpdateDTO.getPrice() != null) {
            order.setPrice(orderUpdateDTO.getPrice());
        }
        if (orderUpdateDTO.getStatus() != null) {
            order.setStatus(orderUpdateDTO.getStatus());
        }

        Order updatedOrder = orderRepository.save(order);
        logger.info("Order updated successfully with id: {}", updatedOrder.getId());

        return convertToDTO(updatedOrder);
    }

    public void deleteOrder(Long orderId) {
        logger.info("Deleting order with id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with id: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });

        orderRepository.delete(order);
        logger.info("Order deleted successfully with id: {}", orderId);
    }

    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        logger.info("Updating order status for order id: {} to {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with id: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        logger.info("Order status updated successfully for order id: {}", orderId);

        return convertToDTO(updatedOrder);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderNumber(order.getOrderNumber());
        orderDTO.setUserId(order.getUserId());
        orderDTO.setProductName(order.getProductName());
        orderDTO.setQuantity(order.getQuantity());
        orderDTO.setPrice(order.getPrice());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCreatedAt(order.getCreatedAt());
        orderDTO.setUpdatedAt(order.getUpdatedAt());
        return orderDTO;
    }
}