package com.example.SupplyChainManagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "distri_orders")
public class DistriOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "distributor_id", referencedColumnName = "distributor_id", nullable = false)
    private Distributor distributor;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id", referencedColumnName = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @Column(name = "order_date") // updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate orderDate;

    @Column(name = "status", length = 50)
    private String status;
    
    @Column(name = "deliver_status", length = 50)
    private String deliverStatus;
    
    @Column(name = "transaction_status", length = 50)
    private String transactionStatus;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @OneToMany(mappedBy = "distriOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DistriOrderItem> orderItems;
    
    @Column(name = "deliver_date")
    private LocalDate deliverDate;
    
    @ManyToOne
    @JoinColumn(name = "payment_doid", referencedColumnName = "pay_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DistriOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<DistriOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

	public String getDeliverStatus() {
		return deliverStatus;
	}

	public void setDeliverStatus(String deliverStatus) {
		this.deliverStatus = deliverStatus;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public LocalDate getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(LocalDate deliverDate) {
		this.deliverDate = deliverDate;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	public void updateStatus() {
        if ("Delivered".equalsIgnoreCase(this.deliverStatus) && "Paid".equalsIgnoreCase(this.transactionStatus)) {
            this.status = "Completed";
        }
    }
	
}