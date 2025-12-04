package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.ProductSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSalesRepository extends JpaRepository<ProductSales, Long> {

    @Query("SELECT SUM(ps.revenue) FROM ProductSales ps WHERE ps.supplier.id = :id")
    Double getTotalRevenue(Long id);

    @Query("SELECT SUM(ps.quantitySold) FROM ProductSales ps WHERE ps.supplier.id = :id")
    Integer getTotalProductsSold(Long id);

    @Query("SELECT AVG(ps.customerSatisfaction) FROM ProductSales ps WHERE ps.supplier.id = :id")
    Double getCustomerSatisfaction(Long id);

    /* @Query("SELECT MONTH(ps.saleDate), SUM(ps.quantitySold) FROM ProductSales ps WHERE ps.supplier.id = :id GROUP BY MONTH(ps.saleDate)")
    List<Object[]> getMonthlySalesData(Long id); */

    @Query("SELECT MONTHNAME(ps.saleDate), MONTH(ps.saleDate) FROM ProductSales ps WHERE ps.supplier.id = :id GROUP BY MONTH(ps.saleDate), MONTHNAME(ps.saleDate) ORDER BY MONTH(ps.saleDate)")
    List<Object[]> getMonthlySalesLabels(Long id);

    List<ProductSales> findBySupplierSupplierId(Long supplierId);
    
    @Query("SELECT MONTHNAME(ps.saleDate) AS month, SUM(ps.quantitySold) AS totalQuantity " +
    	       "FROM ProductSales ps " +
    	       "WHERE ps.supplier.supplierId = :supplierId " +
    	       "GROUP BY MONTHNAME(ps.saleDate), MONTH(ps.saleDate) " + // Group by both month name and month number
    	       "ORDER BY MONTH(ps.saleDate)") // Ensure months are ordered correctly
    	List<Object[]> getMonthlySalesData(@Param("supplierId") Long supplierId);
}