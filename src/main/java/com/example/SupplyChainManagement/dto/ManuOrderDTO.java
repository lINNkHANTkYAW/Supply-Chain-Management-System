package com.example.SupplyChainManagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ManuOrderDTO {
    private Long id;
    private String manuName;
    private LocalDate orderDate;
    private LocalDate deliverDate;
	private String itemNames;
    private Long manuId;
    private Long manuUserId;
    private String status;
    private String transactionStatus = "Not Paid";
    private String deliverStatus = "Not Delivered";
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getManuName() {
		return manuName;
	}
	public void setManuName(String manuName) {
		this.manuName = manuName;
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
	public Long getManuId() {
		return manuId;
	}
	public void setManuId(Long manuId) {
		this.manuId = manuId;
	}
	public Long getManuUserId() {
		return manuUserId;
	}
	public void setManuUserId(Long manuUserId) {
		this.manuUserId = manuUserId;
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
