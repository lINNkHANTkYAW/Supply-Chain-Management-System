package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.DistriProductSales;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.RatingNotification;
import com.example.SupplyChainManagement.repository.CusOrderRepository;
import com.example.SupplyChainManagement.repository.CusOrderItemRepository;
import com.example.SupplyChainManagement.repository.CustomerRepository;
import com.example.SupplyChainManagement.repository.DistriProductSalesRepository;
import com.example.SupplyChainManagement.repository.NotificationRepository;
import com.example.SupplyChainManagement.repository.RatingNotificationRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.Notification;

@Service
public class CustomerOrderService {

    private final CusOrderRepository cusOrderRepository;
    private final CusOrderItemRepository cusOrderItemRepository;
    private final DistriProductSalesRepository distriProductSalesRepository;
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate; // For sending notifications
    private final NotificationRepository notificationRepository;
    private final RatingNotificationRepository ratingNotificationRepository;

    public CustomerOrderService(CusOrderRepository cusOrderRepository, 
            CusOrderItemRepository cusOrderItemRepository,
            DistriProductSalesRepository distriProductSalesRepository,
            CustomerRepository customerRepository,
            RestTemplate restTemplate, NotificationRepository notificationRepository,
            RatingNotificationRepository ratingNotificationRepository) {
this.cusOrderRepository = cusOrderRepository;
this.cusOrderItemRepository = cusOrderItemRepository;
this.customerRepository = customerRepository;
this.restTemplate = restTemplate;
this.distriProductSalesRepository = distriProductSalesRepository;
this.notificationRepository = notificationRepository;
this.ratingNotificationRepository = ratingNotificationRepository;
}
    
 // Fetch orders for a specific customer using userId
    public List<CusOrder> getCustomerOrders(Long userId) {
        Optional<Customer> customer = customerRepository.findByUser_UserId(userId);
        if (customer.isEmpty()) {
            throw new RuntimeException("Customer not found for user ID: " + userId);
        }
        return cusOrderRepository.findByCustomer_CustomerId(customer.get().getCustomerId());
    }

    // Get orders for a specific distributor
    public List<CusOrder> getOrdersForDistributor(Long distributorId) {
        return cusOrderRepository.findByDistributorId(distributorId);
    }
    
    public List<CusOrder> getOrdersOfCustomer(Long userId) {
        return cusOrderRepository.findByCustomerUserId(userId);
    }

    // Get order by ID
    public Optional<CusOrder> getOrderById(Long orderId) {
        return cusOrderRepository.findById(orderId);
    }

    public List<CusOrderItem> getOrderItems(Long orderId) {
        return cusOrderItemRepository.findByCusOrder_OrderId(orderId);
    }

    public BigDecimal calculateTotalForOrder(Long orderId) {
        List<CusOrderItem> items = getOrderItems(orderId);
        return items.stream()
            .map(item -> item.getDistriProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void acceptOrder(Long orderId, String deliveryDate, List<Map<String, Object>> updatedItems, Long distributorUserId) {
        CusOrder order = cusOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        LocalDate localDate = LocalDate.parse(deliveryDate, DateTimeFormatter.ISO_LOCAL_DATE);
        order.setDeliverDate(localDate);
        order.setStatus("Accepted");
        order.setDeliverStatus("Not delivered");
        order.setTransactionStatus("Not paid");

        updatedItems.forEach(itemData -> {
            Long itemId = Long.valueOf((String) itemData.get("id"));
            Integer quantity = Integer.parseInt((String) itemData.get("quantity"));
            CusOrderItem item = cusOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));
            item.setQuantity(quantity);
            int newStock = item.getDistriProduct().getStockQuantity() - quantity;
            if (newStock < 0) {
                throw new RuntimeException("Insufficient stock for " + item.getDistriProduct().getName());
            }
            item.getDistriProduct().setStockQuantity(newStock);
            cusOrderItemRepository.save(item);
        });

        cusOrderRepository.save(order);

        StringBuilder message = new StringBuilder();
        message.append(String.format("Your order #%d has been accepted. Delivery Date: %s\n", orderId, deliveryDate));
        message.append("Order Details:\n");
        order.getOrderItems().forEach(item -> {
            message.append(String.format("- %s: %d units at MMK %s each (Total: MMK %s)\n",
                item.getDistriProduct().getName(),
                item.getQuantity(),
                item.getDistriProduct().getPrice().toString(),
                item.getDistriProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).toString()));
        });
        BigDecimal totalAmount = calculateTotalForOrder(orderId);
        message.append(String.format("Total Amount: MMK %s", totalAmount.toString()));

        Long receiverId = order.getCustomer().getUser() != null ? order.getCustomer().getUser().getUserId() : null;
        if (receiverId != null) {
            try {
                sendNotification(distributorUserId, receiverId, message.toString());
            } catch (Exception e) {
                System.err.println("Failed to send notification for order #" + orderId + ": " + e.getMessage());
            }
        } else {
            System.err.println("Skipping notification: Customer userId is null for order #" + orderId);
        }
    }
    
    @Transactional
    public void declineOrder(Long orderId, String reason, Long distributorUserId) {
        CusOrder order = cusOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        Long customerUserId = order.getCustomer().getUser().getUserId();

        // Delete order items first due to foreign key constraints
        List<CusOrderItem> items = cusOrderItemRepository.findByCusOrder_OrderId(orderId);
        cusOrderItemRepository.deleteAll(items);

        // Delete the order
        cusOrderRepository.delete(order);

        // Send notification to customer
        String message = String.format("Your order #%d has been declined. Reason: %s", orderId, reason);
        sendNotification(distributorUserId, customerUserId, message);
    }

    private void sendNotification(Long senderId, Long receiverId, String message) {
        try {
            // Build URL with query parameters
            URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/chat/send")
                .queryParam("senderId", senderId)
                .queryParam("receiverId", receiverId)
                .queryParam("text", message)
                .build()
                .toUri();

            restTemplate.postForEntity(uri, null, String.class);
            System.out.println("Notification sent: " + message);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
            throw new RuntimeException("Notification sending failed", e);
        }
    }
    
    /* @Transactional
    public void updateOrderStatus(Long orderId, String deliverStatus, String transactionStatus) {
        CusOrder order = cusOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setDeliverStatus(deliverStatus);
        order.setTransactionStatus(transactionStatus);
        cusOrderRepository.save(order);
    } */
    
    /* public void updateOrderStatus(Long orderId, String deliverStatus, String transactionStatus) {
        CusOrder order = cusOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setDeliverStatus(deliverStatus);
        order.setTransactionStatus(transactionStatus);
        order.updateStatus(); // Sets status to "Completed" if both are "Delivered" and "Paid"

        if ("Completed".equals(order.getStatus())) {
            // Create a new DistriProductSales record
            DistriProductSales salesRecord = new DistriProductSales();
            salesRecord.setDistributor(order.getOrderItems().get(0).getDistriProduct().getDistributor());
            salesRecord.setQuantitySold(order.getOrderItems().stream().mapToInt(CusOrderItem::getQuantity).sum());
            salesRecord.setRevenue(order.getOrderItems().stream()
                    .mapToDouble(item -> item.getDistriProduct().getPrice().doubleValue() * item.getQuantity())
                    .sum());
            salesRecord.setSaleDate(LocalDateTime.now());
            distriProductSalesRepository.save(salesRecord);
            createRatingNotification(order);
        }

        cusOrderRepository.save(order);
    }
    
    private void createRatingNotification(CusOrder order) {
        Notification notification = new Notification();
        notification.setOrder(order);
        notification.setUser(order.getCustomer().getUser()); // Customer who placed the order
        notification.setMessage("Please rate your recent order #" + order.getOrderId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRated(false); // Initially, the order is not rated

        notificationRepository.save(notification);
    } */
    
    @Transactional
    public void updateOrderStatus(Long orderId, String deliverStatus, String transactionStatus) {
        CusOrder order = cusOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setDeliverStatus(deliverStatus);
        order.setTransactionStatus(transactionStatus);
        order.updateStatus();

        if ("Completed".equals(order.getStatus())) {
            // Populate DistriProductSales
            Map<Long, List<CusOrderItem>> itemsByDistributor = order.getOrderItems().stream()
                .collect(Collectors.groupingBy(item -> item.getDistriProduct().getDistributor().getDistributorId()));
            
            itemsByDistributor.forEach((distributorId, items) -> {
                DistriProductSales sales = new DistriProductSales();
                Distributor distributor = items.get(0).getDistriProduct().getDistributor();
                sales.setDistributor(distributor);
                sales.setQuantitySold(items.stream().mapToInt(CusOrderItem::getQuantity).sum());
                sales.setRevenue(items.stream()
                    .mapToDouble(item -> item.getQuantity() * item.getDistriProduct().getPrice().doubleValue())
                    .sum());
                sales.setCustomerSatisfaction(0.0);
                sales.setSaleDate(LocalDateTime.now());
                distriProductSalesRepository.save(sales);

                // Send rating notification to customer
                RatingNotification notification = new RatingNotification();
                notification.setCusOrder(order);
                notification.setUser(order.getCustomer().getUser());
                notification.setDistributor(distributor);
                notification.setOrderTitle("Order #" + order.getOrderId());
                notification.setOrderDate(order.getOrderDate());
                notification.setRated(false);
                ratingNotificationRepository.save(notification);
            });
        }

        cusOrderRepository.save(order);
    }

    
}