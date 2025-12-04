package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.CusOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CusOrderRepository extends JpaRepository<CusOrder, Long> {
    List<CusOrder> findByCustomer_CustomerId(Long customerId);
    
    @Query("SELECT o FROM CusOrder o JOIN o.orderItems i WHERE i.distriProduct.distributor.user.userId = :distributorId")
    List<CusOrder> findByDistributorId(@Param("distributorId") Long distributorId);
    
    @Query("SELECT o FROM CusOrder o WHERE o.customer.user.userId = :userId")
    List<CusOrder> findByCustomerUserId(@Param("userId") Long userId);
    
 // Count orders by status and distributor ID
    @Query("SELECT COUNT(o) FROM CusOrder o JOIN o.orderItems i WHERE o.status = :status AND i.distriProduct.distributor.distributorId = :distributorId")
    long countByStatusAndDistributor(@Param("status") String status, @Param("distributorId") Long distributorId);

    // Count total orders by distributor ID
    @Query("SELECT COUNT(o) FROM CusOrder o JOIN o.orderItems i WHERE i.distriProduct.distributor.distributorId = :distributorId")
    long countByDistributor(@Param("distributorId") Long distributorId);
}
