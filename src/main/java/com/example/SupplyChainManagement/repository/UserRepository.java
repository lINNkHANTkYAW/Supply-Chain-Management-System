package com.example.SupplyChainManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Find user by email

	Optional<User> findByEmailAndRole(String email, String role);
	
	@Query("SELECT c FROM Customer c WHERE c.user.userId = :userId")
    Optional<Customer> findCustomerByUserId(Long userId);
	
	Optional<User> findByUsername(String username);
}

