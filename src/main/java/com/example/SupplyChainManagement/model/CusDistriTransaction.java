package com.example.SupplyChainManagement.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "cus_distri_transaction")
public class CusDistriTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "cus_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "distri_id", referencedColumnName = "distributor_id", nullable = false)
    private Distributor distributor;

    @ManyToOne
    @JoinColumn(name = "cd_order_id", referencedColumnName = "order_id", nullable = false)
    private CusOrder cusOrder;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "rating", nullable = false)
    private int rating = 0;
    
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	// Constructors, Getters & Setters
    public CusDistriTransaction() {}

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public CusOrder getCusOrder() {
        return cusOrder;
    }

    public void setCusOrder(CusOrder cusOrder) {
        this.cusOrder = cusOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CusOrderItem item : this.getCusOrder().getOrderItems()) {
            BigDecimal quantity = new BigDecimal(item.getQuantity());  // Convert quantity to BigDecimal
            BigDecimal price = item.getDistriProduct().getPrice();  // Assuming price is BigDecimal
            totalAmount = totalAmount.add(quantity.multiply(price));  // Multiply and add to total
        }
        return totalAmount;
    }
}
