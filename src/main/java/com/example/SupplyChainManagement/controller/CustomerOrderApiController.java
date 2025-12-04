package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CusOrderRepository;
import com.example.SupplyChainManagement.repository.DistriProductSalesRepository;
import com.example.SupplyChainManagement.service.CustomerOrderService;
import com.example.SupplyChainManagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderApiController {

    private final CustomerOrderService customerOrderService;
    private final DistriProductSalesRepository distriProductSalesRepository;
    private final CusOrderRepository cusOrderRepository;
    private final UserService userService;
    // private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CustomerOrderApiController(CustomerOrderService customerOrderService, UserService userService, 
    		DistriProductSalesRepository distriProductSalesRepository, CusOrderRepository cusOrderRepository) {
        this.customerOrderService = customerOrderService;
        this.userService = userService;
        this.distriProductSalesRepository = distriProductSalesRepository;
        this.cusOrderRepository = cusOrderRepository;
    }

    // Get all orders for the logged-in distributor
    @GetMapping
    public List<Map<String, Object>> getOrders(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        List<CusOrder> orders = customerOrderService.getOrdersForDistributor(user.getUserId());
        return formatOrdersResponse(orders);
    }
    
    @GetMapping("/customer")
    public List<Map<String, Object>> getOrdersOfCustomer(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        List<CusOrder> orders = customerOrderService.getCustomerOrders(user.getUserId());
        return formatOrdersResponse(orders);
    }

    // Get specific order details
    @GetMapping("/{orderId}")
    public Map<String, Object> getOrderDetails(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        CusOrder order = customerOrderService.getOrderById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        return formatOrderDetails(order);
    }

    @PostMapping("/accept/{orderId}")
    public ResponseEntity<String> acceptOrder(@PathVariable Long orderId, 
                                            @RequestBody Map<String, Object> requestData,
                                            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        String deliveryDate = (String) requestData.get("deliveryDate");
        List<Map<String, Object>> items = (List<Map<String, Object>>) requestData.get("items");
        customerOrderService.acceptOrder(orderId, deliveryDate, items, user.getUserId());
        return ResponseEntity.ok("Order accepted successfully");
    }

    // Decline an order
    @DeleteMapping("/decline/{orderId}")
    public ResponseEntity<String> declineOrder(@PathVariable Long orderId, 
                                             @RequestBody Map<String, String> requestData,
                                             HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        String reason = requestData.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reason is required to decline an order");
        }

        customerOrderService.declineOrder(orderId, reason, user.getUserId());
        return ResponseEntity.ok("Order declined successfully");
    }

    // Get customer profile
    @GetMapping("/customers/{customerId}")
    public Map<String, Object> getCustomerProfile(@PathVariable Long customerId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        Customer customer = userService.getCustomerByUserId(customerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("customerId", customer.getCustomerId());
        profile.put("name", customer.getUser().getUsername());
        profile.put("address", customer.getAddress());
        profile.put("contactInfo", customer.getContactInfo());
        profile.put("profileImage", customer.getUser().getProfileImg() != null ? 
            customer.getUser().getProfileImg() : "/img/default-profile.png");
        return profile;
    }

    /* private List<Map<String, Object>> formatOrdersResponse(List<CusOrder> orders) {
        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getOrderId());
            orderMap.put("customerId", order.getCustomer().getCustomerId());
            orderMap.put("customerUserId", order.getCustomer().getUser().getUserId());
            orderMap.put("customerName", order.getCustomer().getUser().getUsername());
            orderMap.put("orderDate", order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            orderMap.put("deliverDate", order.getDeliverDate() != null ? order.getDeliverDate().format(formatter) : null); 
            orderMap.put("transactionStatus", order.getTransactionStatus());
            orderMap.put("paymentMethod", order.getPaymentMethod().getPayMethodName());
            orderMap.put("status", order.getStatus());
            orderMap.put("items", order.getOrderItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getOrderItemId());
                itemMap.put("productId", item.getDistriProduct().getDistributorId());
                itemMap.put("name", item.getDistriProduct().getName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getDistriProduct().getPrice());
                itemMap.put("availableStock", item.getDistriProduct().getStockQuantity());
                itemMap.put("distributorId", item.getDistriProduct().getDistributorId());
                itemMap.put("distributorName", item.getDistriProduct().getDistributor().getCompanyName());
                return itemMap;
            }).collect(Collectors.toList()));
            return orderMap;
        }).collect(Collectors.toList());
    } */

    private Map<String, Object> formatOrderDetails(CusOrder order) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", order.getOrderId());
        orderMap.put("customerUserId", order.getCustomer().getUser().getUserId()); // Added for notification
        orderMap.put("items", order.getOrderItems().stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getOrderItemId());
            itemMap.put("name", item.getDistriProduct().getName());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("total", item.getDistriProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemMap.put("availableStock", item.getDistriProduct().getStockQuantity());
            return itemMap;
        }).collect(Collectors.toList()));
        orderMap.put("totalAmount", customerOrderService.calculateTotalForOrder(order.getOrderId()));
        orderMap.put("paymentMethod", order.getPaymentMethod().getPayMethodName());
        orderMap.put("address", order.getCustomer().getAddress());
        orderMap.put("note", order.getNote());
        orderMap.put("orderDate", order.getOrderDate() != null ? 
                order.getOrderDate().format(DATE_FORMATTER) : null); // Use DATE_FORMATTER for LocalDate
            orderMap.put("deliveryDate", order.getDeliverDate() != null ? 
                order.getDeliverDate().format(DATE_FORMATTER) : null);
        return orderMap;
    }
    
    @PutMapping("/update/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> requestData,
            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        String deliverStatus = requestData.get("deliverStatus");
        String transactionStatus = requestData.get("transactionStatus");
        customerOrderService.updateOrderStatus(orderId, deliverStatus, transactionStatus);
        return ResponseEntity.ok("Order status updated successfully");
    }

    private List<Map<String, Object>> formatOrdersResponse(List<CusOrder> orders) {
        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getOrderId());
            orderMap.put("customerId", order.getCustomer().getCustomerId());
            orderMap.put("customerUserId", order.getCustomer().getUser().getUserId());
            orderMap.put("customerName", order.getCustomer().getUser().getUsername());
            orderMap.put("orderDate", order.getOrderDate() != null ? 
                    order.getOrderDate().format(DATE_FORMATTER) : null); // Use DATE_FORMATTER for LocalDate
                orderMap.put("deliverDate", order.getDeliverDate() != null ? 
                    order.getDeliverDate().format(DATE_FORMATTER) : null);
            orderMap.put("status", order.getStatus());
            orderMap.put("deliverStatus", order.getDeliverStatus());
            orderMap.put("transactionStatus", order.getTransactionStatus());
            orderMap.put("paymentMethod", order.getPaymentMethod().getPayMethodName());
            orderMap.put("address", order.getShippingAddress());
            orderMap.put("note", order.getNote());
            orderMap.put("items", order.getOrderItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getOrderItemId());
                itemMap.put("name", item.getDistriProduct().getName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("availableStock", item.getDistriProduct().getStockQuantity());
                itemMap.put("price", item.getDistriProduct().getPrice().doubleValue());
                itemMap.put("total", item.getDistriProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue());
                itemMap.put("productId", item.getDistriProduct().getProductId());
                itemMap.put("distributorId", item.getDistriProduct().getDistributor().getDistributorId());
                return itemMap;
            }).collect(Collectors.toList()));
            return orderMap;
        }).collect(Collectors.toList());
    }
    
    @GetMapping("/stats/{distributorId}")
    public Map<String, Object> getStats(@PathVariable Long distributorId) {
        int productsSold = distriProductSalesRepository.sumQuantitySoldByDistributor(distributorId);
        double netProfit = distriProductSalesRepository.sumRevenueByDistributor(distributorId);
        long pendingOrders = cusOrderRepository.countByStatusAndDistributor("Pending", distributorId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("productsSold", productsSold);
        stats.put("netProfit", netProfit);
        stats.put("pendingOrders", pendingOrders);
        return stats;
    }
    
    @GetMapping("/monthly-sales/{distributorId}")
    public List<Object[]> getMonthlySales(@PathVariable Long distributorId) {
        return distriProductSalesRepository.findMonthlySalesByDistributor(distributorId);
    }
    
    @GetMapping("/completion-rate/{distributorId}")
    public Map<String, Long> getCompletionRate(@PathVariable Long distributorId) {
        long completedOrders = cusOrderRepository.countByStatusAndDistributor("Completed", distributorId);
        long totalOrders = cusOrderRepository.countByDistributor(distributorId);
        Map<String, Long> completionRate = new HashMap<>();
        completionRate.put("completed", completedOrders);
        completionRate.put("total", totalOrders);
        return completionRate;
    }
    
    
}