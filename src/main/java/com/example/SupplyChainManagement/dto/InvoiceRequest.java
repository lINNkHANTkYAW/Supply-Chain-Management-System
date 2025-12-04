package com.example.SupplyChainManagement.dto;

import com.example.SupplyChainManagement.model.InvoiceItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceRequest {
    private LocalDate orderDate; // Use LocalDateTime
    private LocalDate deliverDate; // Use LocalDate
    private Long paymentMethodId;
    private Long sellerId;
    private Long buyerId;
    private List<InvoiceItemRequest> materials;
    private BigDecimal totalAmount;

    // Getters and Setters
    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(LocalDate deliverDate) {
        this.deliverDate = deliverDate;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public List<InvoiceItemRequest> getMaterials() {
        return materials;
    }

    public void setMaterials(List<InvoiceItemRequest> materials) {
        this.materials = materials;
    }

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
}