package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "manu_orders")
public class ManuOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id", referencedColumnName = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @Column(name = "deliver_date", nullable = false)
    private LocalDate deliverDate;

    @Column(name = "status", length = 50)
    private String status;
    
    @Column(name = "deliver_status", length = 50)
    private String deliverStatus;
    
    @Column(name = "transaction_status", length = 50)
    private String transactionStatus;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "manuOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManuOrderItem> orderItems;
    
    @ManyToOne
    @JoinColumn(name = "payment_moid", referencedColumnName = "pay_method_id", nullable = false)
    private PaymentMethod paymentMethod;
    
    @OneToOne
    @JoinColumn(name = "invoice_id", referencedColumnName = "invoice_id", nullable = false)
    private Invoice invoice;

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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

    public List<ManuOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<ManuOrderItem> orderItems) {
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

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	
}
