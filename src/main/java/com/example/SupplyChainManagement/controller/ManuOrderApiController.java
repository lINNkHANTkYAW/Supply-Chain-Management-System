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
import com.example.SupplyChainManagement.model.ManuOrder;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.PaymentMethod;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.service.CustomerOrderService;
import com.example.SupplyChainManagement.service.DistriOrderService;
import com.example.SupplyChainManagement.service.ManuOrderService;
import com.example.SupplyChainManagement.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/manufacturer/orders")
public class ManuOrderApiController {

	@Autowired
	private SupplierRepository supplierRepository;
	private final ManuOrderService manuOrderService;
    private final UserService userService;
    // private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ManuOrderApiController(ManuOrderService manuOrderService, UserService userService) {
        this.manuOrderService = manuOrderService;
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
    public List<Map<String, Object>> getOrdersOfManufacturer(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        List<ManuOrder> orders = manuOrderService.getManufacturerOrders(user.getUserId());
        return formatOrdersResponse(orders);
    }
    
    @GetMapping("/supplier")
    public List<Map<String, Object>> getOrdersForSupplier(HttpSession session) {
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
        System.out.println("Order: " + order);
        System.out.println("Order Items: " + order.getOrderItems());
        
        return formatOrderDetails(order);
    }
    
    
    
    private List<Map<String, Object>> formatOrdersResponse(List<ManuOrder> orders) {
        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getOrderId());
            orderMap.put("manufacturerId", order.getManufacturer().getManufacturerId());
            orderMap.put("manufacturerUserId", order.getManufacturer().getUser().getUserId());
            orderMap.put("companyName", order.getManufacturer().getUser().getUsername());
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
                itemMap.put("name", item.getSupplierRawMaterial().getName());
                itemMap.put("quantity", item.getQuantity());
                //itemMap.put("qtyOnHand", item.getSupplierRawMaterial().getQtyOnHand());
                //itemMap.put("unitPrice", item.getSupplierRawMaterial().getUnitPrice());
                //itemMap.put("total", item.getSupplierRawMaterial().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue());
                itemMap.put("rawMaterialSid", item.getSupplierRawMaterial().getRawMaterialSid());
                itemMap.put("supplierId", item.getSupplierRawMaterial().getSupplier().getSupplierId());
                return itemMap;
            }).collect(Collectors.toList()));
            return orderMap;
        }).collect(Collectors.toList());
    }
    
    private Map<String, Object> formatOrderDetails(ManuOrder order) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", order.getOrderId());
        orderMap.put("manufacturerUserId", order.getManufacturer().getUser().getUserId()); // Added for notification
        orderMap.put("items", order.getOrderItems().stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getOrderItemId());
            itemMap.put("rawMaterialSid", item.getSupplierRawMaterial().getRawMaterialSid());
            itemMap.put("name", item.getSupplierRawMaterial().getName());
            itemMap.put("quantity", item.getQuantity());
            // itemMap.put("total", item.getSupplierRawMaterial().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemMap.put("availableStock", item.getSupplierRawMaterial().getQtyOnHand());
            return itemMap;
        }).collect(Collectors.toList()));
        // orderMap.put("totalAmount", manuOrderService.calculateTotalForOrder(order.getOrderId()));
        orderMap.put("paymentMethod", order.getPaymentMethod().getPayMethodName());
        orderMap.put("address", order.getManufacturer().getAddress());
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

        manuOrderService.updateOrderStatus(orderId, deliverStatus, transactionStatus);

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
    
    /* @GetMapping("/monthly-sales/{manufacturerId}")
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
    } */
}
