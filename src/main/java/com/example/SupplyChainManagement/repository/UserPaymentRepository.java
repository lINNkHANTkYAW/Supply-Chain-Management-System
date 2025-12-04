package com.example.SupplyChainManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.SupplyChainManagement.dto.PaymentMethodDTO;
import com.example.SupplyChainManagement.model.PaymentMethod;
import com.example.SupplyChainManagement.model.UserPayment;

public interface UserPaymentRepository extends JpaRepository<UserPayment, Long>{

	@Query("SELECT up.paymentMethod FROM UserPayment up WHERE up.user.userId = :userId")
    List<PaymentMethodDTO> findPaymentMethodsByUserId(@Param("userId") Long userId);
	
	List<UserPayment> findByUser_UserId(Long userId);
}
