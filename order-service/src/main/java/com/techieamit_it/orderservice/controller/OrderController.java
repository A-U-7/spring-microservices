package com.techieamit_it.orderservice.controller;


import com.techieamit_it.orderservice.dto.OrderCreateDTO;
import com.techieamit_it.orderservice.dto.OrderDTO;
import com.techieamit_it.orderservice.dto.OrderUpdateDTO;
import com.techieamit_it.orderservice.model.OrderStatus;
import com.techieamit_it.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Endpoints for managing orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        
        logger.info("POST /api/orders - Creating new order");
        OrderDTO createdOrder = orderService.createOrder(orderCreateDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves an order by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long orderId) {
        
        logger.info("GET /api/orders/{} - Fetching order by ID", orderId);
        OrderDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/order-number/{orderNumber}")
    @Operation(summary = "Get order by order number", description = "Retrieves an order by its order number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> getOrderByOrderNumber(
            @Parameter(description = "Order Number", required = true)
            @PathVariable String orderNumber) {
        
        logger.info("GET /api/orders/order-number/{} - Fetching order by order number", orderNumber);
        OrderDTO order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieves all orders with pagination support")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        
        logger.info("GET /api/orders - Fetching all orders with pagination");
        Page<OrderDTO> orders = orderService.getOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all orders (list)", description = "Retrieves all orders as a list without pagination")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderDTO>> getAllOrdersList() {
        
        logger.info("GET /api/orders/list - Fetching all orders as list");
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user ID", description = "Retrieves all orders for a specific user")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        
        logger.info("GET /api/orders/user/{} - Fetching orders by user ID", userId);
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}/paged")
    @Operation(summary = "Get orders by user ID (paged)", description = "Retrieves orders for a specific user with pagination")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<Page<OrderDTO>> getOrdersByUserIdPaged(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        
        logger.info("GET /api/orders/user/{}/paged - Fetching orders by user ID with pagination", userId);
        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieves all orders with a specific status")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(
            @Parameter(description = "Order Status", required = true)
            @PathVariable OrderStatus status) {
        
        logger.info("GET /api/orders/status/{} - Fetching orders by status", status);
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}/paged")
    @Operation(summary = "Get orders by status (paged)", description = "Retrieves orders with a specific status and pagination")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatusPaged(
            @Parameter(description = "Order Status", required = true)
            @PathVariable OrderStatus status,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        
        logger.info("GET /api/orders/status/{}/paged - Fetching orders by status with pagination", status);
        Page<OrderDTO> orders = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update order", description = "Updates an existing order with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> updateOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateDTO orderUpdateDTO) {
        
        logger.info("PUT /api/orders/{} - Updating order", orderId);
        OrderDTO updatedOrder = orderService.updateOrder(orderId, orderUpdateDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        
        logger.info("PATCH /api/orders/{}/status - Updating order status to {}", orderId, status);
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order", description = "Deletes an order permanently")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long orderId) {
        
        logger.info("DELETE /api/orders/{} - Deleting order", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}