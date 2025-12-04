package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "cus_orders")
public class CusOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "order_date")
    private LocalDate orderDate;
    
    @Column(name = "deliver_date")
    private LocalDate deliverDate;

	@Column(name = "status", nullable = false)
    private String status;
	
	@Column(name = "transaction_status", nullable = false)
    private String transactionStatus = "Not Paid";
	
	@Column(name = "deliver_status", nullable = false)
    private String deliverStatus = "Not Delivered";

    @OneToMany(mappedBy = "cusOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CusOrderItem> orderItems = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "payment_oid", referencedColumnName = "pay_method_id", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Column(name = "note") 
    private String note;

    @Column(name = "shipping_address")
    private String shippingAddress;
    

    public void updateStatus() {
        if ("Delivered".equalsIgnoreCase(deliverStatus) && "Paid".equalsIgnoreCase(transactionStatus)) {
            this.status = "Completed";
        } else {
            this.status = "Pending";
        }
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public LocalDate getDeliverDate() { return deliverDate; }
    public void setDeliverDate(LocalDate deliverDate) { this.deliverDate = deliverDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (!"Completed".equals(status) && !"Pending".equals(status) && !"Accepted".equals(status)) {
            throw new IllegalArgumentException("Status must be 'Completed', 'Pending', or 'Accepted'");
        }
        this.status = status;
    }
    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
    public String getDeliverStatus() { return deliverStatus; }
    public void setDeliverStatus(String deliverStatus) { this.deliverStatus = deliverStatus; }
    public List<CusOrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<CusOrderItem> orderItems) { this.orderItems = orderItems; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
