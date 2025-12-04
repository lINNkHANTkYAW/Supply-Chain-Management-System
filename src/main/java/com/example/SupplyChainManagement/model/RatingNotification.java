package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "rating_notifications")
public class RatingNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonManagedReference
    private CusOrder cusOrder;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "distributor_id", nullable = false)
    @JsonManagedReference
    private Distributor distributor;

    @Column(name = "order_title", nullable = false)
    private String orderTitle;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "rated", nullable = false)
    private boolean rated;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CusOrder getCusOrder() { return cusOrder; }
    public void setCusOrder(CusOrder cusOrder) { this.cusOrder = cusOrder; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Distributor getDistributor() { return distributor; }
    public void setDistributor(Distributor distributor) { this.distributor = distributor; }
    public String getOrderTitle() { return orderTitle; }
    public void setOrderTitle(String orderTitle) { this.orderTitle = orderTitle; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate localDate) { this.orderDate = localDate; }
    public boolean isRated() { return rated; }
    public void setRated(boolean rated) { this.rated = rated; }
}