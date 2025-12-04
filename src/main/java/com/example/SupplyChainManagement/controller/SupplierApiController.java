package com.example.SupplyChainManagement.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.service.ManuProductService;
import com.example.SupplyChainManagement.service.ManufacturerService;
import com.example.SupplyChainManagement.service.SupplierService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierApiController {
	
	private final SupplierService supplierService;
    // private final ManuProductService manuProductService;

    public SupplierApiController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

	@GetMapping("/{supplierId}")
    public ResponseEntity<Map<String, Object>> getSupplierProfile(@PathVariable Long supplierId) {
        Optional<Supplier> supplierOptional = supplierService.getSupplierById(supplierId);

        if (supplierOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Supplier supplier = supplierOptional.get();

        // Convert to JSON-friendly format
        Map<String, Object> supplierMap = new HashMap<>();
        supplierMap.put("supplierId", supplier.getSupplierId());
        supplierMap.put("supplierUserId", supplier.getUser().getUserId());
        supplierMap.put("companyName", supplier.getCompanyName());
        supplierMap.put("contactInfo", supplier.getContactInfo());
        supplierMap.put("address", supplier.getAddress());
        supplierMap.put("email", supplier.getUser().getEmail());
        supplierMap.put("profileImg", supplier.getUser().getProfileImg());

        // Include user details
        if (supplier.getUser() != null) {
        	supplierMap.put("email", supplier.getUser().getEmail()); // ✅ Now email is included
        } else {
        	supplierMap.put("email", "N/A");
        }

        return ResponseEntity.ok(supplierMap);
    }
	
	 @GetMapping("/session-user")
	    public ResponseEntity<?> getSessionUser(HttpSession session) {
	        User loggedInUser = (User) session.getAttribute("loggedInUser");

	        if (loggedInUser == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not logged in"));
	        }

	        Optional<Supplier> supplier = supplierService.findByUserId(loggedInUser.getUserId());

	        if (supplier.isPresent()) {
	            return ResponseEntity.ok(supplier.get().getSupplierId());  // Return only customerId
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Supplier not found"));
	        }
	    }
}
