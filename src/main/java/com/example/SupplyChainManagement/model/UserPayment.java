package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_payment")
public class UserPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "up_id")
    private Long upId;

    @ManyToOne
    @JoinColumn(name = "user_payid", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "payment_uid", referencedColumnName = "pay_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    // Constructors
    public UserPayment() {}

    public UserPayment(User user, PaymentMethod paymentMethod) {
        this.user = user;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Long getUpId() {
        return upId;
    }

    public void setUpId(Long upId) {
        this.upId = upId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
