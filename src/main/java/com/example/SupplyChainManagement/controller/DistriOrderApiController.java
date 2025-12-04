package com.example.SupplyChainManagement.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.DistriOrder;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.PaymentMethod;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;
import com.example.SupplyChainManagement.service.CustomerOrderService;
import com.example.SupplyChainManagement.service.DistriOrderService;
import com.example.SupplyChainManagement.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/distributor/orders")
public class DistriOrderApiController {

	@Autowired
	private ManufacturerRepository manufacturerRepository;
	private final DistriOrderService distriOrderService;
    private final UserService userService;
    // private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DistriOrderApiController(DistriOrderService distriOrderService, UserService userService) {
        this.distriOrderService = distriOrderService;
        this.userService = userService;
    }

    // Get all orders for the logged-in distributor
    /* @GetMapping
    public List<Map<String, Object>> getOrders(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        List<DistriOrder> orders = distriOrderService.getOrdersForDistributor(user.getUserId());
        return formatOrdersResponse(orders);
    } */  // not finished 
    
    @GetMapping
    public List<Map<String, Object>> getOrdersOfDistributor(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        List<DistriOrder> orders = distriOrderService.getDistributorOrders(user.getUserId());
        return formatOrdersResponse(orders);
    }
    
    @GetMapping("/manu")
    public List<Map<String, Object>> getOrdersForManufacturer(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        List<DistriOrder> orders = distriOrderService.getOrdersForManufacturer(user.getUserId());
        return formatOrdersResponse(orders);
    }

    // Get specific order details
    @GetMapping("/{orderId}")
    public Map<String, Object> getOrderDetails(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        DistriOrder order = distriOrderService.getOrderById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        System.out.println("Order: " + order);
        System.out.println("Order Items: " + order.getOrderItems());
        
        return formatOrderDetails(order);
    }
    
    
    
    private List<Map<String, Object>> formatOrdersResponse(List<DistriOrder> orders) {
        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getOrderId());
            orderMap.put("distributorId", order.getDistributor().getDistributorId());
            orderMap.put("distributorUserId", order.getDistributor().getUser().getUserId());
            orderMap.put("companyName", order.getDistributor().getUser().getUsername());
            orderMap.put("orderDate", order.getOrderDate());
            orderMap.put("deliverDate", order.getDeliverDate() != null ? order.getDeliverDate() : null);
            orderMap.put("status", order.getStatus());
            orderMap.put("deliverStatus", order.getDeliverStatus());
            orderMap.put("transactionStatus", order.getTransactionStatus());
            PaymentMethod paymentMethod = order.getPaymentMethod();
            orderMap.put("paymentMethod", paymentMethod != null ? paymentMethod.getPayMethodName() : "N/A");
            orderMap.put("items", order.getOrderItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getOrderItemId());
                itemMap.put("name", item.getManuProduct().getName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("availableStock", item.getManuProduct().getStockQuantity());
                itemMap.put("price", item.getManuProduct().getPrice().doubleValue());
                itemMap.put("total", item.getManuProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue());
                itemMap.put("productId", item.getManuProduct().getProductMid());
                itemMap.put("manufacturerId", item.getManuProduct().getManufacturer().getManufacturerId());
                return itemMap;
            }).collect(Collectors.toList()));
            return orderMap;
        }).collect(Collectors.toList());
    }
    
    private Map<String, Object> formatOrderDetails(DistriOrder order) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", order.getOrderId());
        orderMap.put("distributorUserId", order.getDistributor().getUser().getUserId()); // Added for notification
        orderMap.put("items", order.getOrderItems().stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getOrderItemId());
            itemMap.put("productId", item.getManuProduct().getProductMid());
            itemMap.put("name", item.getManuProduct().getName());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("total", item.getManuProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemMap.put("availableStock", item.getManuProduct().getStockQuantity());
            return itemMap;
        }).collect(Collectors.toList()));
        orderMap.put("totalAmount", distriOrderService.calculateTotalForOrder(order.getOrderId()));
        orderMap.put("paymentMethod", order.getPaymentMethod().getPayMethodName());
        orderMap.put("address", order.getDistributor().getAddress());
        orderMap.put("orderDate", order.getOrderDate());
        orderMap.put("deliveryDate", order.getDeliverDate() != null ? 
            order.getDeliverDate() : null);
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

        distriOrderService.updateOrderStatus(orderId, deliverStatus, transactionStatus);

        return ResponseEntity.ok("Order status updated successfully");
    }
    
 // Get monthly sales data
    /* @GetMapping("/monthly-sales")
    public List<Object[]> getMonthlySales(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        Optional<Manufacturer> manuOpt = manufacturerRepository.findByUser(user);
        return distriOrderService.getMonthlySales(manuOpt.get().getManufacturerId());
    }

    // Get order completion rate
    @GetMapping("/completion-rate")
    public Map<String, Integer> getOrderCompletionRate(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        Optional<Manufacturer> manuOpt = manufacturerRepository.findByUser(user);
        return distriOrderService.getOrderCompletionRate(manuOpt.get().getManufacturerId());
    }
    
    @GetMapping("/stats/{manufacturerId}")
    public ResponseEntity<Map<String, Object>> getManufacturerStats(@PathVariable Long manufacturerId) {
        Map<String, Object> stats = distriOrderService.getManufacturerStats(manufacturerId);
        return ResponseEntity.ok(stats);
    } */
    
    @GetMapping("/monthly-sales/{manufacturerId}")
    public ResponseEntity<List<Object[]>> getMonthlySales(@PathVariable Long manufacturerId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        Optional<Manufacturer> manuOpt = manufacturerRepository.findByUser(user);
        if (!manuOpt.isPresent() || !manuOpt.get().getManufacturerId().equals(manufacturerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to this manufacturer's data");
        }
        List<Object[]> monthlySales = distriOrderService.getMonthlySales(manufacturerId);
        return ResponseEntity.ok(monthlySales);
    }

    @GetMapping("/completion-rate/{manufacturerId}")
    public ResponseEntity<Map<String, Integer>> getOrderCompletionRate(@PathVariable Long manufacturerId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        Optional<Manufacturer> manuOpt = manufacturerRepository.findByUser(user);
        if (!manuOpt.isPresent() || !manuOpt.get().getManufacturerId().equals(manufacturerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to this manufacturer's data");
        }
        Map<String, Integer> completionRate = distriOrderService.getOrderCompletionRate(manufacturerId);
        return ResponseEntity.ok(completionRate);
    }

    @GetMapping("/stats/{manufacturerId}")
    public ResponseEntity<Map<String, Object>> getManufacturerStats(@PathVariable Long manufacturerId) {
        Map<String, Object> stats = distriOrderService.getManufacturerStats(manufacturerId);
        return ResponseEntity.ok(stats);
    }
}
