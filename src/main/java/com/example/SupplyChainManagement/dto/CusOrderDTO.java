package com.example.SupplyChainManagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CusOrderDTO {
    private Long id;
    private String customerName;
    private LocalDate orderDate;
    private LocalDate deliverDate;
	private String itemNames;
    private Long customerId;
    private Long customerUserId;
    private String status;
    private String transactionStatus = "Not Paid";
    private String deliverStatus = "Not Delivered";

    public Long getCustomerUserId() {
		return customerUserId;
	}
	public void setCustomerUserId(Long customerUserId) {
		this.customerUserId = customerUserId;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public LocalDate getDeliverDate() {
		return deliverDate;
	}
	public void setDeliverDate(LocalDate localDate) {
		this.deliverDate = localDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public String getDeliverStatus() {
		return deliverStatus;
	}
	public void setDeliverStatus(String deliverStatus) {
		this.deliverStatus = deliverStatus;
	}
	// Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate localDate) { this.orderDate = localDate; }
    public String getItemNames() { return itemNames; }
    public void setItemNames(String itemNames) { this.itemNames = itemNames; }
}
