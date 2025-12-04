package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "distri_product_sales")
public class DistriProductSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "distributor_id")
    private Distributor distributor;

    @Column(name = "quantity_sold")
    private int quantitySold;

    @Column(name = "revenue")
    private double revenue;

    @Column(name = "customer_satisfaction")
    private double customerSatisfaction;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public int getQuantitySold() {
		return quantitySold;
	}

	public void setQuantitySold(int quantitySold) {
		this.quantitySold = quantitySold;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}

	public double getCustomerSatisfaction() {
		return customerSatisfaction;
	}

	public void setCustomerSatisfaction(double customerSatisfaction) {
		this.customerSatisfaction = customerSatisfaction;
	}

	public LocalDateTime getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(LocalDateTime saleDate) {
		this.saleDate = saleDate;
	}

    // Getters and Setters
   
    
}