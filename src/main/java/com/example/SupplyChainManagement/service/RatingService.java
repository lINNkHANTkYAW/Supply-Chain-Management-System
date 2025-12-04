package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.dto.NotificationDTO;
import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.Rating;
import com.example.SupplyChainManagement.model.RatingNotification;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CusOrderRepository;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import com.example.SupplyChainManagement.repository.NotificationRepository;
import com.example.SupplyChainManagement.repository.RatingNotificationRepository;
import com.example.SupplyChainManagement.repository.RatingRepository;
import com.example.SupplyChainManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {
	
	private final RatingRepository ratingRepository;
    private final RatingNotificationRepository ratingNotificationRepository;
    private final DistributorRepository distributorRepository;
    private final CusOrderRepository cusOrderRepository;
    private final UserRepository userRepository;
    
    public RatingService(RatingRepository ratingRepository,
            RatingNotificationRepository ratingNotificationRepository,
            DistributorRepository distributorRepository,
            CusOrderRepository cusOrderRepository,
            UserRepository userRepository) {
this.ratingRepository = ratingRepository;
this.ratingNotificationRepository = ratingNotificationRepository;
this.distributorRepository = distributorRepository;
this.cusOrderRepository = cusOrderRepository;
this.userRepository = userRepository;
}
    @Transactional
    public void submitRating(Long orderId, Integer ratingValue, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CusOrder order = cusOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        RatingNotification notification = ratingNotificationRepository.findByCusOrderOrderIdAndUser(orderId, user);
        if (notification == null || notification.isRated()) {
            throw new IllegalStateException("No pending rating or already rated");
        }

        // Save rating
        Rating rating = new Rating(order, ratingValue, user);
        rating.setCreatedAt(LocalDateTime.now());
        ratingRepository.save(rating);

        // Update notification
        notification.setRated(true);
        ratingNotificationRepository.save(notification);

        // Update distributor rating (average)
        Distributor distributor = notification.getDistributor();
        double newRating = ((distributor.getRating() * distributor.getRatingCount()) + ratingValue) /
                          (distributor.getRatingCount() + 1);
        distributor.setRating(newRating);
        distributor.setRatingCount(distributor.getRatingCount() + 1);
        distributorRepository.save(distributor);
    }

    public List<RatingNotification> getPendingNotifications(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ratingNotificationRepository.findByUserAndRatedFalse(user);
    }

    /* @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private CusOrderRepository cusOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Fetch unrated customer orders as notifications
    public List<NotificationDTO> getNotifications() {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        Long userId = loggedInUser.getUserId();
        List<CusOrder> orders = cusOrderRepository.findByCustomerUserId(userId);

        return orders.stream()
            .filter(order -> !ratingRepository.existsByCusOrderOrderIdAndUserUserId(order.getOrderId(), userId))
            .map(order -> new NotificationDTO(
                order.getOrderId(),
                "Order #" + order.getOrderId(),
                order.getOrderDate().format(DATE_FORMATTER)
            ))
            .collect(Collectors.toList());
    }

    // Submit a rating for a customer order
    @Transactional
    public void submitRating(Long orderId, Integer rating) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        CusOrder cusOrder = cusOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (ratingRepository.existsByCusOrderOrderIdAndUserUserId(orderId, loggedInUser.getUserId())) {
            throw new RuntimeException("Order already rated");
        }

        Rating ratingEntity = new Rating(cusOrder, rating, loggedInUser);
        ratingRepository.save(ratingEntity);
    } */
	
	/* private final RatingRepository ratingRepository;
    private final DistributorRepository distributorRepository;
    private final NotificationRepository notificationRepository;

    public RatingService(RatingRepository ratingRepository, DistributorRepository distributorRepository,
                         NotificationRepository notificationRepository) {
        this.ratingRepository = ratingRepository;
        this.distributorRepository = distributorRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void submitRating(Long orderId, int ratingValue, Long userId) {
        CusOrder order = cusOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Distributor distributor = order.getOrderItems().get(0).getDistriProduct().getDistributor();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a new rating
        Rating rating = new Rating();
        rating.setCusOrder(order);
        rating.setRating(ratingValue);
        rating.setUser(user);
        rating.setCreatedAt(LocalDateTime.now());
        ratingRepository.save(rating);

        // Update the distributor's average rating
        updateDistributorRating(distributor);

        // Mark the notification as rated
        Notification notification = notificationRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRated(true);
        notificationRepository.save(notification);
    }

    private void updateDistributorRating(Distributor distributor) {
        List<Rating> ratings = ratingRepository.findByCusOrder_OrderItems_DistriProduct_Distributor(distributor);
        double averageRating = ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
        distributor.setRating(averageRating);
        distributorRepository.save(distributor);
    } */
}