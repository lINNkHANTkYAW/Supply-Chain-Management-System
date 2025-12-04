package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.DistriOrder;
import com.example.SupplyChainManagement.model.ManuOrder;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManuOrderRepository extends JpaRepository<ManuOrder, Long> {
	@Query("SELECT o FROM ManuOrder o JOIN o.orderItems i WHERE i.supplierRawMaterial.supplier.user.userId = :supplierId")
	List<ManuOrder> findBySupplierId(@Param("supplierId") Long supplierId);

	@Query("SELECT MONTH(o.orderDate) as month, SUM(oi.quantity * r.unitPrice) as sales " +
	           "FROM ManuOrder o JOIN o.orderItems oi " +
	           "JOIN oi.supplierRawMaterial r " +
	           "WHERE r.supplier.supplierId = :supplierId " +
	           "GROUP BY MONTH(o.orderDate)")
	    List<Object[]> findMonthlySales(@Param("supplierId") Long supplierId);


	@Query("SELECT COUNT(o) FROM ManuOrder o WHERE o.manufacturer.manufacturerId = :manufacturerId AND o.status = :status")
	Integer countByStatus(Long manufacturerId, String status);
	
	@Query("SELECT o.status, COUNT(o.orderId) FROM ManuOrder o WHERE o.supplier.supplierId = :supplierId AND (o.status = 'Completed' OR o.status = 'Pending') GROUP BY o.status")
    List<Object[]> getOrderCompletionRate(Long supplierId);
    
    @Query("SELECT COUNT(o) FROM ManuOrder o WHERE o.status = :status AND o.supplier.supplierId = :supplierId")
    Integer countByStatusAndSupplierId(@Param("status") String status, @Param("supplierId") Long supplierId);
    
    List<ManuOrder> findByDeliverStatusAndTransactionStatus(String deliverStatus, String transactionStatus);
    
    List<ManuOrder> findByManufacturer_ManufacturerIdAndStatus(Long manufacturerId, String status);
    
    //ADDED
    List<ManuOrder> findByManufacturer_ManufacturerId(Long manufacturerId);
}