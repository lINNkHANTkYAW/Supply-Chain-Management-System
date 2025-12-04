package com.example.SupplyChainManagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "cus_order_items")
public class CusOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "cus_order_id",referencedColumnName = "order_id", nullable = false)
    @JsonBackReference
    private CusOrder cusOrder;

    @ManyToOne
    @JoinColumn(name = "distri_product_id", referencedColumnName = "product_did", nullable = false)
    private DistriProduct distriProduct;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Constructors, Getters & Setters
    public CusOrderItem() {}

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public CusOrder getCusOrder() {
        return cusOrder;
    }

    public void setCusOrder(CusOrder cusOrder) {
        this.cusOrder = cusOrder;
    }

    public DistriProduct getDistriProduct() {
        return distriProduct;
    }

    public void setDistriProduct(DistriProduct distriProduct) {
        this.distriProduct = distriProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
