package com.example.SupplyChainManagement.dto;

public class MaterialDetail {
    private Long rawMaterialSid;
    private Integer qtyOnHand;
    private Double unitPrice;
	public Long getRawMaterialSid() {
		return rawMaterialSid;
	}
	public void setRawMaterialSid(Long rawMaterialSid) {
		this.rawMaterialSid = rawMaterialSid;
	}
	public Integer getQtyOnHand() {
		return qtyOnHand;
	}
	public void setQtyOnHand(Integer qtyOnHand) {
		this.qtyOnHand = qtyOnHand;
	}
	public Double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

    // Getters and Setters
}