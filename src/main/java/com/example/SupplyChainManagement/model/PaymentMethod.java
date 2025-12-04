package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "payment_method")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_method_id")
    private Long payMethodId;

    @Column(name = "pay_method_name", nullable = false, length = 100)
    private String payMethodName;

    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents circular reference
    private List<CusOrder> orders;
    
    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents circular reference
    private List<DistriOrder> distriOrders;
    
    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents circular reference
    private List<ManuOrder> manuOrders;

    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true)  
    @JsonIgnore
    private List<UserPayment> userPayments;

    // Constructors
    public PaymentMethod() {}

    public PaymentMethod(String payMethodName) {
        this.payMethodName = payMethodName;
    }

    // Getters and Setters
    public Long getPayMethodId() {
        return payMethodId;
    }

    public void setPayMethodId(Long payMethodId) {
        this.payMethodId = payMethodId;
    }

    public String getPayMethodName() {
        return payMethodName;
    }

    public void setPayMethodName(String payMethodName) {
        this.payMethodName = payMethodName;
    }

    public List<CusOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<CusOrder> orders) {
        this.orders = orders;
    }

    public List<UserPayment> getUserPayments() {
        return userPayments;
    }

    public void setUserPayments(List<UserPayment> userPayments) {
        this.userPayments = userPayments;
    }

	public List<DistriOrder> getDistriOrders() {
		return distriOrders;
	}

	public void setDistriOrders(List<DistriOrder> distriOrders) {
		this.distriOrders = distriOrders;
	}

	public List<ManuOrder> getManuOrders() {
		return manuOrders;
	}

	public void setManuOrders(List<ManuOrder> manuOrders) {
		this.manuOrders = manuOrders;
	}
}
