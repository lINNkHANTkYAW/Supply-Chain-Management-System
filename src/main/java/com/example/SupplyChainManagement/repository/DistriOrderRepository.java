package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.DistriOrder;
import com.example.SupplyChainManagement.model.ManuOrder;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DistriOrderRepository extends JpaRepository<DistriOrder, Long> {

	/* @Query("SELECT MONTH(o.orderDate) as month, SUM(oi.quantity * p.price) as sales "
			+ "FROM DistriOrder o JOIN o.orderItems oi " + "JOIN oi.manuProduct p "
			+ "WHERE YEAR(o.orderDate) = YEAR(CURRENT_DATE) " + "GROUP BY MONTH(o.orderDate)")
	List<Object[]> findMonthlySales(); */

	/* @Query("SELECT COUNT(o) FROM DistriOrder o WHERE o.status = ?1")
	Integer countByStatus(String status); */

	@Query("SELECT o FROM DistriOrder o JOIN o.distributor d WHERE d.user.userId = :distributorUserId")
	List<DistriOrder> findByDistributorUserId(Long distributorUserId);
	
	List<DistriOrder> findByDistributor_DistributorId(Long distributorId);
	
	/* @Query("SELECT o FROM DistriOrder o JOIN o.orderItems i WHERE i.manuProduct.manufacturer.user.userId = :manufacturerId")
    List<DistriOrder> findByManufacturerId(@Param("manufacturerId") Long manufacturerId); */
	
	Optional<DistriOrder> findByOrderId(Long orderId);
	
	// Get monthly sales data for the current year
    /* @Query("SELECT MONTH(o.orderDate) as month, SUM(oi.quantity * p.price) as sales " +
           "FROM DistriOrder o JOIN o.orderItems oi " +
           "JOIN oi.manuProduct p " +
           "WHERE YEAR(o.orderDate) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(o.orderDate)")
    List<Object[]> findMonthlySales(Long manufacturerId); */
    
 // Count orders by status
    /* @Query("SELECT COUNT(o) FROM DistriOrder o WHERE o.status = :status")
    Integer countByStatus(@Param("status") String status); */
    
    /* @Query("SELECT o.status, COUNT(o.orderId) FROM DistriOrder o WHERE o.manufacturer.manufacturerId = :manufacturerId AND (o.status = 'Completed' OR o.status = 'Pending') GROUP BY o.status")
    List<Object[]> getOrderCompletionRate(@Param("manufacturerId") Long manufacturerId); */
    
	@Query("SELECT o FROM DistriOrder o JOIN o.orderItems i WHERE i.manuProduct.manufacturer.user.userId = :manufacturerId")
    List<DistriOrder> findByManufacturerId(@Param("manufacturerId") Long manufacturerId);

    @Query("SELECT COUNT(o) FROM DistriOrder o WHERE o.status = :status AND o.manufacturer.manufacturerId = :manufacturerId")
    Integer countByStatusAndManufacturerId(@Param("status") String status, @Param("manufacturerId") Long manufacturerId);

    @Query("SELECT o.status, COUNT(o.orderId) FROM DistriOrder o WHERE o.manufacturer.manufacturerId = :manufacturerId AND (o.status = 'Completed' OR o.status = 'Pending') GROUP BY o.status")
    List<Object[]> getOrderCompletionRate(@Param("manufacturerId") Long manufacturerId);

    // Updated query for monthly sales of completed orders
    @Query("SELECT MONTH(o.orderDate) as month, SUM(oi.quantity * p.price) as sales " +
           "FROM DistriOrder o JOIN o.orderItems oi " +
           "JOIN oi.manuProduct p " +
           "WHERE o.manufacturer.manufacturerId = :manufacturerId " +
           "AND o.status = 'Completed' " +
           "AND YEAR(o.orderDate) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(o.orderDate)")
    List<Object[]> findMonthlySalesForCompletedOrders(@Param("manufacturerId") Long manufacturerId);
    
}