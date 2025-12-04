package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.service.CustomerService;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerApiController {

    private final CustomerService customerService;

    public CustomerApiController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getCustomerProfile(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        Optional<Customer> customer = customerService.findByUserId(loggedInUser.getUserId());
        if (customer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Customer not found"));
        }

        // Fetch profile image as a file path
        String profileImgPath = loggedInUser.getProfileImg(); // Now stored as a String (file path)

        Map<String, String> response = Map.of(
            "username", customer.get().getName(),
            "email", loggedInUser.getEmail(),
            "contactInfo", customer.get().getContactInfo(),
            "address", customer.get().getAddress(),
            "profileImg", (profileImgPath != null && !profileImgPath.isEmpty()) ? profileImgPath : "/img/default-profile.png"
        );

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/getCustomerId/{userId}")
    public ResponseEntity<Long> getCustomerId(@PathVariable Long userId) {
        Optional<Customer> customer = customerService.findByUserId(userId);
        return customer.map(c -> ResponseEntity.ok(c.getCustomerId()))
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable Long customerId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        Optional<Customer> customerOpt = customerService.findByCustomerId(customerId);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Customer not found"));
        }

        Customer customer = customerOpt.get();
        User customerUser = customer.getUser();
        String profileImgPath = customerUser.getProfileImg();

        Map<String, Object> response = new HashMap<>();
        response.put("name", customer.getName());
        response.put("address", customer.getAddress());
        response.put("contactInfo", customer.getContactInfo());
        response.put("profileImage", (profileImgPath != null && !profileImgPath.isEmpty()) ? profileImgPath : "/img/default-profile.png");

        return ResponseEntity.ok(response);
    }


    /* @GetMapping("/api/session-user")
    public ResponseEntity<?> getSessionUser(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not logged in"));
        }
        Optional<Customer> customer = customerService.findByUserId(loggedInUser.getUserId());

        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customer.get().getCustomerId());
        response.put("username", customer.get().getName());
        response.put("email", loggedInUser.getEmail());
        response.put("contactInfo", customer.get().getContactInfo());

        return ResponseEntity.ok(response);
    } */
    
    @GetMapping("/session-user")
    public ResponseEntity<?> getSessionUser(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not logged in"));
        }

        Optional<Customer> customer = customerService.findByUserId(loggedInUser.getUserId());

        if (customer.isPresent()) {
            return ResponseEntity.ok(customer.get().getCustomerId());  // Return only customerId
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Customer not found"));
        }
    }



}