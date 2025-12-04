package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUserUserId(Long userId);
    boolean existsByCusOrderOrderIdAndUserUserId(Long orderId, Long userId);
}