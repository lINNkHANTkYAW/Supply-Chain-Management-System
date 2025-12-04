package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "manu_invoice")
public class ManuInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manu_invoice_id")
    private Long manuInvoiceId;

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "user_id", nullable = false)
    private User buyer;

    @Column(name = "invoice_date", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate invoiceDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate; // Use LocalDateTime

    @Column(name = "deliver_date", nullable = false) // Ensure this field is NOT NULL
    private LocalDate deliverDate; // Use LocalDate

    @ManyToOne
    @JoinColumn(name = "payment_method_id", referencedColumnName = "pay_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy = "manuInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManuInvoiceItem> manuInvoiceItems;

    @OneToOne(mappedBy = "manuInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatMessage chatMessage;
    
    @Column(name = "invoice_image_url", nullable = false)
    private String invoiceImageUrl;

    public String getInvoiceImageUrl() {
		return invoiceImageUrl;
	}

	public void setInvoiceImageUrl(String invoiceImageUrl) {
		this.invoiceImageUrl = invoiceImageUrl;
	}

	// Getters and Setters
    public Long getManuInvoiceId() {
        return manuInvoiceId;
    }

    public void setManuInvoiceId(Long invoiceId) {
        this.manuInvoiceId = invoiceId;
    }

    public List<ManuInvoiceItem> getManuInvoiceItems() {
		return manuInvoiceItems;
	}

	public void setManuInvoiceItems(List<ManuInvoiceItem> manuInvoiceItems) {
		this.manuInvoiceItems = manuInvoiceItems;
	}

	public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(LocalDate deliveredDatetime) {
        this.deliverDate = deliveredDatetime;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<ManuInvoiceItem> getInvoiceItems() {
        return manuInvoiceItems;
    }

    public void setInvoiceItems(List<ManuInvoiceItem> manuInvoiceItems) {
        this.manuInvoiceItems = manuInvoiceItems;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}