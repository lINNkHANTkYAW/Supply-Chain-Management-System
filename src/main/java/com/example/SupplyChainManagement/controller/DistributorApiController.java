package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.service.DistributorService;
import com.example.SupplyChainManagement.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/distributors")
public class DistributorApiController {

    private final DistributorService distributorService;
    private final ProductService productService;

    public DistributorApiController(DistributorService distributorService, ProductService productService) {
        this.distributorService = distributorService;
        this.productService = productService;
    }

    /**
     * Get a distributor's profile by ID.
     * @param distributorId Distributor ID.
     * @return Distributor profile details.
     */
    @GetMapping("/{distributorId}")
    public ResponseEntity<Map<String, Object>> getDistributorProfile(@PathVariable Long distributorId) {
        Optional<Distributor> distributorOptional = distributorService.getDistributorById(distributorId);

        if (distributorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Distributor distributor = distributorOptional.get();

        // Convert to JSON-friendly format
        Map<String, Object> distributorMap = new HashMap<>();
        distributorMap.put("distributorId", distributor.getDistributorId());
        distributorMap.put("companyName", distributor.getCompanyName());
        distributorMap.put("contactInfo", distributor.getContactInfo());
        distributorMap.put("address", distributor.getAddress());
        distributorMap.put("email", distributor.getUser().getEmail());
        distributorMap.put("profileImg", distributor.getUser().getProfileImg());
        distributorMap.put("rating", distributor.getRating());

        // Include user details
        if (distributor.getUser() != null) {
            distributorMap.put("email", distributor.getUser().getEmail()); // ✅ Now email is included
        } else {
            distributorMap.put("email", "N/A");
        }

        return ResponseEntity.ok(distributorMap);
    }


    /**
     * Get distributor details for a specific product.
     * @param productId Product ID.
     * @return Distributor details for the product.
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<Distributor> getDistributorForProduct(@PathVariable Long productId) {
        Optional<DistriProduct> product = productService.getProductById(productId);
        
        if (product.isEmpty() || product.get().getDistributor() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product.get().getDistributor());
    }
}
