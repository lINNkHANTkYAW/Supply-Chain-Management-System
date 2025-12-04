package com.example.SupplyChainManagement.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "manu_invoice_items")
public class ManuInvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_item_id")
    private Long invoiceItemId;

    @ManyToOne
    @JoinColumn(name = "manu_invoice_id", referencedColumnName = "manu_invoice_id", nullable = false)
    private ManuInvoice manuInvoice;

    @ManyToOne
    @JoinColumn(name = "manu_product_id", referencedColumnName = "product_mid", nullable = false)
    private ManuProduct manuProduct;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    // Getters and Setters
    public Long getInvoiceItemId() {
        return invoiceItemId;
    }

    public void setInvoiceItemId(Long invoiceItemId) {
        this.invoiceItemId = invoiceItemId;
    }

    public ManuInvoice getInvoice() {
        return manuInvoice;
    }

    public void setInvoice(ManuInvoice manuInvoice) {
        this.manuInvoice = manuInvoice;
    }

    public ManuProduct getManuProduct() {
        return manuProduct;
    }

    public void setManuProduct(ManuProduct manuProduct) {
        this.manuProduct = manuProduct;
    }

    public ManuInvoice getManuInvoice() {
		return manuInvoice;
	}

	public void setManuInvoice(ManuInvoice manuInvoice) {
		this.manuInvoice = manuInvoice;
	}

	public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}