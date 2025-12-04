package com.example.SupplyChainManagement.dto;

import java.math.BigDecimal;

public class ManuInvoiceItemRequest {
    private Long productMid;
    private int quantity;
    private BigDecimal unitPrice;
    
    
	public Long getManuProductId() {
		return productMid;
	}
	public void setManuProductId(Long manuProductId) {
		this.productMid = manuProductId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
    
}

