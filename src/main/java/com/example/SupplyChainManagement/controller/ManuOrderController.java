package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.ManuOrder;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.service.CustomerOrderService;
import com.example.SupplyChainManagement.service.ManuOrderService;
import com.example.SupplyChainManagement.service.UserService;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/manu/orders")
public class ManuOrderController {

    
    private ManuOrderService manuOrderService;
    private final UserService userService;
    
    public ManuOrderController(ManuOrderService manuOrderService, UserService userService) {
        this.manuOrderService = manuOrderService;
        this.userService = userService;
    }

    @PostMapping
    public ManuOrder createOrder(@RequestBody ManuOrder order) {
        return manuOrderService.createOrder(order);
    }
    
    @GetMapping
    public List<Map<String, Object>> getOrders(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        List<ManuOrder> orders = manuOrderService.getOrdersForSupplier(user.getUserId());
        return formatOrdersResponse(orders);
    }
    
 // Get specific order details
    @GetMapping("/{orderId}")
    public Map<String, Object> getOrderDetails(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        ManuOrder order = manuOrderService.getOrderById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        return formatOrderDetails(order);
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
        manuOrderService.updateOrderStatus(orderId, deliverStatus, transactionStatus);
        return ResponseEntity.ok("Order status updated successfully");
    }
    
    private List<Map<String, Object>> formatOrdersResponse(List<ManuOrder> orders) {
        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getOrderId());
            orderMap.put("manuId", order.getManufacturer().getManufacturerId());
            orderMap.put("manuUserId", order.getManufacturer().getUser().getUserId());
            orderMap.put("manuName", order.getManufacturer().getUser().getUsername());
            orderMap.put("orderDate", order.getOrderDate());
            orderMap.put("deliverDate", order.getDeliverDate() != null ? order.getDeliverDate() : null);
            orderMap.put("status", order.getStatus());
            orderMap.put("deliverStatus", order.getDeliverStatus());
            orderMap.put("transactionStatus", order.getTransactionStatus());
            orderMap.put("paymentMethod", order.getPaymentMethod().getPayMethodName());
            orderMap.put("items", order.getOrderItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getOrderItemId());
                itemMap.put("name", item.getSupplierRawMaterial().getName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("availableStock", item.getSupplierRawMaterial().getQtyOnHand());
                itemMap.put("price", item.getSupplierRawMaterial().getUnitPrice().doubleValue());
                itemMap.put("total", item.getSupplierRawMaterial().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue());
                itemMap.put("productId", item.getSupplierRawMaterial().getRawMaterialSid());
                itemMap.put("distributorId", item.getSupplierRawMaterial().getSupplier().getSupplierId());
                return itemMap;
            }).collect(Collectors.toList()));
            return orderMap;
        }).collect(Collectors.toList());
    }
    
    private Map<String, Object> formatOrderDetails(ManuOrder order) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", order.getOrderId());
        orderMap.put("manuUserId", order.getManufacturer().getUser().getUserId()); // Added for notification
        orderMap.put("orderDate", order.getOrderDate());
        orderMap.put("items", order.getOrderItems().stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getOrderItemId());
            itemMap.put("name", item.getSupplierRawMaterial().getName());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("total", item.getSupplierRawMaterial().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemMap.put("availableStock", item.getSupplierRawMaterial().getQtyOnHand());
            return itemMap;
        }).collect(Collectors.toList()));
        orderMap.put("totalAmount", manuOrderService.calculateTotalForOrder(order.getOrderId()));
        orderMap.put("paymentMethod", order.getPaymentMethod().getPayMethodName());
        orderMap.put("address", order.getManufacturer().getAddress());
        orderMap.put("deliveryDate", order.getDeliverDate() != null ? 
            order.getDeliverDate() : null);
        return orderMap;
    }
}