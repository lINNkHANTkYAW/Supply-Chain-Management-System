package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.DistriProductSales;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DistriProductSalesRepository extends JpaRepository<DistriProductSales, Long> {

    @Query("SELECT COALESCE(SUM(d.quantitySold), 0) FROM DistriProductSales d WHERE d.distributor.distributorId = :distributorId")
    int getTotalProductsSold(@Param("distributorId") Long distributorId);

    @Query("SELECT COALESCE(SUM(d.revenue), 0) FROM DistriProductSales d WHERE d.distributor.distributorId = :distributorId")
    double getTotalRevenue(@Param("distributorId") Long distributorId);
    
 // Sum of quantity sold by distributor
    @Query("SELECT SUM(d.quantitySold) FROM DistriProductSales d WHERE d.distributor.distributorId = :distributorId")
    Integer sumQuantitySoldByDistributor(@Param("distributorId") Long distributorId);

    // Sum of revenue by distributor
    @Query("SELECT SUM(d.revenue) FROM DistriProductSales d WHERE d.distributor.distributorId = :distributorId")
    Double sumRevenueByDistributor(@Param("distributorId") Long distributorId);

    // Monthly sales data by distributor
    @Query("SELECT MONTH(d.saleDate) as month, SUM(d.revenue) as revenue FROM DistriProductSales d WHERE d.distributor.distributorId = :distributorId GROUP BY MONTH(d.saleDate)")
    List<Object[]> findMonthlySalesByDistributor(@Param("distributorId") Long distributorId);
}