package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.service.DistributorService;
import com.example.SupplyChainManagement.service.ManuProductService;
import com.example.SupplyChainManagement.service.ManufacturerService;
import com.example.SupplyChainManagement.service.ProductService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerApiController {

    private final ManufacturerService manufacturerService;
    private final ManuProductService manuProductService;

    public ManufacturerApiController(ManufacturerService manufacturerService, ManuProductService manuProductService) {
        this.manufacturerService = manufacturerService;
        this.manuProductService = manuProductService;
    }

    /**
     * Get a distributor's profile by ID.
     * @param distributorId Distributor ID.
     * @return Distributor profile details.
     */
    @GetMapping("/{manufacturerId}")
    public ResponseEntity<Map<String, Object>> getManufacturerProfile(@PathVariable Long manufacturerId) {
        Optional<Manufacturer> manufacturerOptional = manufacturerService.getManufacturerById(manufacturerId);

        if (manufacturerOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Manufacturer manufacturer = manufacturerOptional.get();

        // Convert to JSON-friendly format
        Map<String, Object> manufacturerMap = new HashMap<>();
        manufacturerMap.put("manufacturerId", manufacturer.getManufacturerId());
        manufacturerMap.put("manuUserId", manufacturer.getUser().getUserId());
        manufacturerMap.put("companyName", manufacturer.getCompanyName());
        manufacturerMap.put("contactInfo", manufacturer.getContactInfo());
        manufacturerMap.put("address", manufacturer.getAddress());
        manufacturerMap.put("email", manufacturer.getUser().getEmail());
        manufacturerMap.put("profileImg", manufacturer.getUser().getProfileImg());

        // Include user details
        if (manufacturer.getUser() != null) {
        	manufacturerMap.put("email", manufacturer.getUser().getEmail()); // ✅ Now email is included
        } else {
        	manufacturerMap.put("email", "N/A");
        }

        return ResponseEntity.ok(manufacturerMap);
    }


    /**
     * Get distributor details for a specific product.
     * @param productId Product ID.
     * @return Distributor details for the product.
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<Manufacturer> getManufacturerForProduct(@PathVariable Long productId) {
        Optional<ManuProduct> product = manuProductService.getProductById(productId);
        
        if (product.isEmpty() || product.get().getManufacturer() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product.get().getManufacturer());
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getCustomerProfile(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        Optional<Manufacturer> manu = manufacturerService.findByUserId(loggedInUser.getUserId());
        if (manu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Manufacturer not found"));
        }

        // Fetch profile image as a file path
        String profileImgPath = loggedInUser.getProfileImg(); // Now stored as a String (file path)

        Map<String, String> response = Map.of(
            "username", manu.get().getCompanyName(),
            "email", loggedInUser.getEmail(),
            "contactInfo", manu.get().getContactInfo(),
            "address", manu.get().getAddress(),
            "profileImg", (profileImgPath != null && !profileImgPath.isEmpty()) ? profileImgPath : "/img/default-profile.png"
        );

        return ResponseEntity.ok(response);
    }
}
