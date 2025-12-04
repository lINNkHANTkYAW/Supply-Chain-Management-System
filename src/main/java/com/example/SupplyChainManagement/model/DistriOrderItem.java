package com.example.SupplyChainManagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "distri_order_items")
public class DistriOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "distri_order_id", referencedColumnName = "order_id", nullable = false)
    @JsonBackReference
    private DistriOrder distriOrder;

    @ManyToOne
    @JoinColumn(name = "manu_product_id", referencedColumnName = "product_mid", nullable = false)
    private ManuProduct manuProduct;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Getters and Setters
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public DistriOrder getDistriOrder() {
        return distriOrder;
    }

    public void setDistriOrder(DistriOrder distriOrder) {
        this.distriOrder = distriOrder;
    }

    public ManuProduct getManuProduct() {
        return manuProduct;
    }

    public void setManuProduct(ManuProduct manuProduct) {
        this.manuProduct = manuProduct;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
