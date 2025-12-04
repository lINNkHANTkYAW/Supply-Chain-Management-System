package com.example.SupplyChainManagement.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SupplyChainManagement.dto.PaymentMethodDTO;
import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.PaymentMethod;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CusOrderRepository;
import com.example.SupplyChainManagement.repository.DistriProductRepository;
import com.example.SupplyChainManagement.service.CustomerOrderService;
import com.example.SupplyChainManagement.service.CustomerService;
import com.example.SupplyChainManagement.service.PaymentMethodService;
import com.example.SupplyChainManagement.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class PlaceOrderController {
	
	private final PaymentMethodService paymentMethodService;
	private final CustomerOrderService customerOrderService;
	private final UserService userService;
	private final CustomerService customerService;
	private final DistriProductRepository distriProductRepository;
	private final CusOrderRepository cusOrderRepository;
	
	public PlaceOrderController(CustomerOrderService cusOrderService, PaymentMethodService paymentMethodService, 
			UserService userService, CustomerService customerService, DistriProductRepository distriProductRepository, CusOrderRepository cusOrderRepository) {
        this.customerOrderService = cusOrderService;
        this.paymentMethodService = paymentMethodService;
        this.userService = userService;
        this.customerService = customerService;
        this.distriProductRepository = distriProductRepository;
        this.cusOrderRepository = cusOrderRepository;
    }
	
	/** ✅ Fetch available payment methods for a distributor */
    /* @GetMapping("/distributor/{distributorId}/payment-methods")
    public ResponseEntity<List<PaymentMethod>> getDistributorPaymentMethods(@PathVariable Long distributorId) {
        List<PaymentMethod> paymentMethods = paymentMethodService.getPaymentMethodsByDistributor(distributorId);
        return ResponseEntity.ok(paymentMethods);
    } */
	
	/** ✅ Fetch available payment methods for a distributor */
    @GetMapping("/distributor/{distributorId}/payment-methods")
    public ResponseEntity<List<PaymentMethodDTO>> getDistributorPaymentMethods(@PathVariable Long distributorId) {
        List<PaymentMethodDTO> paymentMethods = paymentMethodService.getPaymentMethodsByDistributor(distributorId);
        return ResponseEntity.ok(paymentMethods);
    }
    
    /** ✅ Fetch user's shipping address */
    @GetMapping("/user/address")
    public ResponseEntity<?> getUserAddress(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return ResponseEntity.status(401).body("{\"error\": \"User not logged in\"}");
        }

        Optional<Customer> customer = customerService.findByUserId(user.getUserId());

        if (customer.isPresent()) {
            return ResponseEntity.ok().body("{\"address\": \"" + customer.get().getAddress() + "\"}");
        } else {
            return ResponseEntity.status(404).body("{\"error\": \"Customer not found\"}");
        }
    }

    /** ✅ Place an order */
    @PostMapping("/place")
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody CusOrder order, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not logged in"));
        }

        Optional<Customer> customerOpt = customerService.findByUserId(user.getUserId());
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Customer not found"));
        }

        Customer customer = customerOpt.get();
        order.setCustomer(customer); // ✅ Ensure customer is linked

        if (order.getPaymentMethod() == null || order.getPaymentMethod().getPayMethodId() == null) {
            return ResponseEntity.status(400).body(Map.of("error", "🚨 Payment Method ID is missing!"));
        }
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("Pending"); // ✅ Default status
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }

        // ✅ Fix: Validate order items
        List<CusOrderItem> validItems = new ArrayList<>();
        for (CusOrderItem item : order.getOrderItems()) {
            if (item.getDistriProduct() == null || item.getDistriProduct().getProductId() == null) {
                return ResponseEntity.status(400).body(Map.of("error", "🚨 Product ID is missing in one or more items!"));
            }

            Optional<DistriProduct> productOpt = distriProductRepository.findById(item.getDistriProduct().getProductId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "🚨 Product not found! ID: " + item.getDistriProduct().getProductId()));
            }

            item.setCusOrder(order); // ✅ Set order reference
            item.setDistriProduct(productOpt.get()); // ✅ Ensure managed entity
            validItems.add(item);
        }

        if (validItems.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "🚨 Order must contain at least one valid item!"));
        }

        order.setOrderItems(validItems); // ✅ Set valid items

        try {
            cusOrderRepository.save(order); // ✅ Hibernate will cascade & save items
            return ResponseEntity.ok(Map.of("message", "✅ Order placed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "❌ Error saving order: " + e.getMessage()));
        }
    }



}
