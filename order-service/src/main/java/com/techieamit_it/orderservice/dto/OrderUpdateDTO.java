package com.techieamit_it.orderservice.dto;



import com.techieamit_it.orderservice.model.OrderStatus;

import java.math.BigDecimal;

public class OrderUpdateDTO {

    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private OrderStatus status;

    // Constructors
    public OrderUpdateDTO() {
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}