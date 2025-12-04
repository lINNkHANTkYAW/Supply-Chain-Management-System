package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ItemType;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.ItemTypeRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.service.InventoryService;
import com.example.SupplyChainManagement.service.ItemTypeService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class SupplierInventoryApiController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ItemTypeService itemTypeService;
    
    @Autowired
    private ItemTypeRepository itemTypeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    /**
     * ✅ **Fetch All Inventory Items for the Logged-In Supplier**
     */
    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventoryItems(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(user.getUserId());
        if (supplierOptional.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        Supplier supplier = supplierOptional.get();
        List<InventoryItem> inventoryItems = inventoryService.getItemsBySupplierId(supplier.getSupplierId());


        return ResponseEntity.ok(inventoryItems);
    }

    /**
     * ✅ **Fetch a Single Inventory Item by ID**
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }

        Supplier supplier = supplierRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Optional<InventoryItem> itemOptional = inventoryService.getItemById(id);
        if (itemOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        InventoryItem item = itemOptional.get();
        if (!item.getSupplier().getSupplierId().equals(supplier.getSupplierId())) {
            return ResponseEntity.status(403).body(null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", item.getId());
        response.put("name", item.getName());
        response.put("date", item.getDate());
        response.put("quantity", item.getQuantity());
        response.put("cost", item.getCost());
        response.put("perUnitCost", item.getPerUnitCost());
        response.put("itemType", item.getItemType().getId()); // Use ID instead of name
        response.put("category", item.getCategory().getCategoryId()); // Add category ID
        response.put("supplierId", item.getSupplier().getSupplierId());

        return ResponseEntity.ok(response);
    }
    /**
     * ✅ **Update an Inventory Item**
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateItem(@PathVariable Long id, @RequestBody InventoryItem updatedItem, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Supplier supplier = supplierRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Optional<InventoryItem> existingItemOptional = inventoryService.getItemById(id);
        if (existingItemOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        InventoryItem item = existingItemOptional.get();
        item.setName(updatedItem.getName());
        item.setDate(updatedItem.getDate());
        item.setQuantity(updatedItem.getQuantity());
        item.setCost(updatedItem.getCost());
        item.setPerUnitCost(updatedItem.getPerUnitCost());
        item.setItemType(updatedItem.getItemType());

        Category category = categoryRepository.findById(updatedItem.getCategory().getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        item.setCategory(category);

        inventoryService.save(item);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ **Delete an Inventory Item**
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<InventoryItem> getInventoryItemByName(@PathVariable String name) {
        InventoryItem item = inventoryService.findByName(name);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }
   


}
