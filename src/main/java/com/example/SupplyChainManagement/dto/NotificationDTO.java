package com.example.SupplyChainManagement.dto;

public class NotificationDTO {
    private Long orderId;
    private String orderTitle;
    private String orderDate;

    public NotificationDTO(Long orderId, String orderTitle, String orderDate) {
        this.orderId = orderId;
        this.orderTitle = orderTitle;
        this.orderDate = orderDate;
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderTitle() { return orderTitle; }
    public void setOrderTitle(String orderTitle) { this.orderTitle = orderTitle; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
}