package com.example.SupplyChainManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.SupplyChainManagement.model.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
	
	/** ✅ Fetch Payment Methods for a Distributor via User Payment */
    @Query("SELECT pm FROM PaymentMethod pm " +
           "JOIN UserPayment up ON pm.payMethodId = up.paymentMethod.payMethodId " +
           "JOIN User u ON up.user.userId = u.userId " +
           "JOIN Distributor d ON u.userId = d.user.userId " +
           "WHERE d.distributorId = :distributorId")
    List<PaymentMethod> findByDistributorId(@Param("distributorId") Long distributorId);
    
    
}
