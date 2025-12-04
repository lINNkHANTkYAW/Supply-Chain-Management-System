package com.example.SupplyChainManagement.dto;


import java.time.LocalDate;

public class DistriOrderDTO {
    private Long id;
    private String distriName;
    private LocalDate orderDate;
    private LocalDate deliverDate;
	private String itemNames;
    private Long distriId;
    private Long distriUserId;
    private String status;
    private String transactionStatus = "Not Paid";
    private String deliverStatus = "Not Delivered";
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDistriName() {
		return distriName;
	}
	public void setDistriName(String manuName) {
		this.distriName = manuName;
	}
	public LocalDate getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(LocalDate localDate) {
		this.orderDate = localDate;
	}
	public LocalDate getDeliverDate() {
		return deliverDate;
	}
	public void setDeliverDate(LocalDate localDate) {
		this.deliverDate = localDate;
	}
	public String getItemNames() {
		return itemNames;
	}
	public void setItemNames(String itemNames) {
		this.itemNames = itemNames;
	}
	public Long getDistriId() {
		return distriId;
	}
	public void setDistriId(Long manuId) {
		this.distriId = manuId;
	}
	public Long getDistriUserId() {
		return distriUserId;
	}
	public void setDistriUserId(Long manuUserId) {
		this.distriUserId = manuUserId;
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

    
}
