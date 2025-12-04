package com.example.SupplyChainManagement.dto;

import java.math.BigDecimal;

public class InvoiceItemRequest {
    private Long rawMaterialId;
    private int quantity;
    private BigDecimal unitPrice;
    
    
	public Long getRawMaterialId() {
		return rawMaterialId;
	}
	public void setRawMaterialId(Long rawMaterialId) {
		this.rawMaterialId = rawMaterialId;
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

