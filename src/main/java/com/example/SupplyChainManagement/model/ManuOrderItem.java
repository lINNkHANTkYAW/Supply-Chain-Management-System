package com.example.SupplyChainManagement.model;


import jakarta.persistence.*;

@Entity
@Table(name = "manu_order_items")
public class ManuOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "manu_order_id", referencedColumnName = "order_id", nullable = false)
    private ManuOrder manuOrder;

    @ManyToOne
    @JoinColumn(name = "sup_raw_material_id", referencedColumnName = "raw_material_sid", nullable = false)
    private SupplierRawMaterial supplierRawMaterial;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Getters and Setters
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public ManuOrder getManuOrder() {
        return manuOrder;
    }

    public void setManuOrder(ManuOrder manuOrder) {
        this.manuOrder = manuOrder;
    }

    public SupplierRawMaterial getSupplierRawMaterial() {
        return supplierRawMaterial;
    }

    public void setSupplierRawMaterial(SupplierRawMaterial supplierRawMaterial) {
        this.supplierRawMaterial = supplierRawMaterial;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
