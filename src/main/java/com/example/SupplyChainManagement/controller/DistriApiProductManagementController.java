package com.example.SupplyChainManagement.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import com.example.SupplyChainManagement.service.DistributorService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/distributor-inventory")
public class DistriApiProductManagementController {

    @Autowired
    private DistributorService distributorService;

    @Autowired
    private DistributorRepository distributorRepository;
    
    @GetMapping("/marketplace/{id}")
    public ResponseEntity<?> getMarketplaceItem(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User not authenticated", "message", "Please log in to access this resource"));
        }

        Optional<Distributor> distributor = distributorService.getDistributorByUserId(loggedInUser.getUserId());
        if (distributor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Forbidden", "message", "User is not associated with a distributor"));
        }

        Long distributorId = distributor.get().getDistributorId();
        Optional<DistriProduct> itemOptional = distributorService.getMarketplaceItemById(id);
        if (!itemOptional.isPresent() || !itemOptional.get().getDistributor().getDistributorId().equals(distributorId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Not Found", "message", "Item not found or not accessible"));
        }
        return ResponseEntity.ok(itemOptional.get());
    }
}