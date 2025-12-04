/* package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.ManuInventoryItem;
import com.example.SupplyChainManagement.model.ManuOrderItem;
import com.example.SupplyChainManagement.service.ManuInventoryService;
import com.example.SupplyChainManagement.service.ManuOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ManuInventoryController {

    @Autowired
    private ManuInventoryService inventoryService;

    @Autowired
    private ManuOrderItemService manuOrderItemService; // Inject ManuOrderItemService

    @GetMapping("/delivered-items")
    public List<ManuOrderItem> getDeliveredItems() {
    	List<ManuOrderItem> items = manuOrderItemService.getDeliveredItems();
        System.out.println("Delivered items count: " + items.size()); // Debug log
        return items; // Use ManuOrderItemService for delivered items
    }

    @PutMapping("/delivered-items/{id}")
    public ResponseEntity<ManuOrderItem> updateDeliveredItem(@PathVariable Long id, @RequestBody ManuOrderItem updatedItem) {
        ManuOrderItem item = manuOrderItemService.updateDeliveredItem(id, updatedItem); // Update to use ManuOrderItemService
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/delivered-items/{id}")
    public ResponseEntity<Void> deleteDeliveredItem(@PathVariable Long id) {
        manuOrderItemService.deleteDeliveredItem(id); // Update to use ManuOrderItemService
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/manufacturer-inventory")
    public List<ManuInventoryItem> getManufacturerInventory() {
        return inventoryService.getManufacturerInventory();
    }

    @PostMapping("/manufacturer-inventory")
    public ResponseEntity<ManuInventoryItem> addManufacturerItem(@RequestBody ManuInventoryItem item) {
        ManuInventoryItem newItem = inventoryService.addManufacturerItem(item);
        return ResponseEntity.ok(newItem);
    }

    @PutMapping("/manufacturer-inventory/{id}")
    public ResponseEntity<ManuInventoryItem> updateManufacturerItem(@PathVariable Long id, @RequestBody ManuInventoryItem updatedItem) {
        ManuInventoryItem item = inventoryService.updateManufacturerItem(id, updatedItem);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/manufacturer-inventory/{id}")
    public ResponseEntity<Void> deleteManufacturerItem(@PathVariable Long id) {
        inventoryService.deleteManufacturerItem(id);
        return ResponseEntity.noContent().build();
    }
} */