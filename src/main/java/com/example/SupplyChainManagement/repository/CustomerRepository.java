package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser_UserId(Long userId);
    Optional<Customer> findByUser(User user);
}
