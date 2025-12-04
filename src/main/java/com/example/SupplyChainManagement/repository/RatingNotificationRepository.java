package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.RatingNotification;
import com.example.SupplyChainManagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingNotificationRepository extends JpaRepository<RatingNotification, Long> {
    List<RatingNotification> findByUserAndRatedFalse(User user);
    RatingNotification findByCusOrderOrderIdAndUser(Long orderId, User user);
}