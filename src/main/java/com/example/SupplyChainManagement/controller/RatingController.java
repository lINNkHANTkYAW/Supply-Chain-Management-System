package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.*;
import com.example.SupplyChainManagement.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/notifications")
    public List<RatingNotification> getNotifications(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        return ratingService.getPendingNotifications(user.getUserId());
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitRating(@RequestBody RatingRequest request, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        ratingService.submitRating(request.getOrderId(), request.getRating(), user.getUserId());
        return ResponseEntity.ok("Rating submitted successfully");
    }
}

class RatingRequest {
    private Long orderId;
    private Integer rating;

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}

/* package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.dto.NotificationDTO;
import com.example.SupplyChainManagement.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        try {
            List<NotificationDTO> notifications = ratingService.getNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(null); // Unauthorized if not logged in
        }
    }

    @PostMapping("/rate")
    public ResponseEntity<Void> submitRating(
            @RequestParam("orderId") Long orderId,
            @RequestParam("rating") Integer rating) {
        try {
            ratingService.submitRating(orderId, rating);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    } 
} */