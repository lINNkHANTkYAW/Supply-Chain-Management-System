package com.example.SupplyChainManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.SupplyChainManagement.model.DistriInventory;
import com.example.SupplyChainManagement.model.DistriStockProduct;
import com.example.SupplyChainManagement.service.DistriInventoryService;
import com.example.SupplyChainManagement.service.DistriStockProductService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DistriInventoryController {

    @Autowired
    private DistriStockProductService stockProductService;
    
    @Autowired
    private DistriInventoryService inventoryService;

    @GetMapping("/distri/delivered-items")
    public List<DistriInventory> getDeliveredItems(@RequestParam Long distributorId) {
        return inventoryService.getDeliveredItems(distributorId);
    }

    @GetMapping("/distri/inventory-items")
    public List<DistriStockProduct> getInventoryItems(@RequestParam Long distributorId) {
        return stockProductService.getInventoryItems(distributorId);
    }

    @GetMapping("/distri/item/{itemId}")
    public DistriStockProduct getInventoryItem(@PathVariable Long itemId) {
        return stockProductService.getInventoryItem(itemId);
    }

    @PostMapping("/distri/inventory-items")
    public DistriStockProduct addInventoryItem(@RequestBody DistriStockProduct item, @RequestParam Long distributorId) {
        return stockProductService.saveInventoryItem(item, distributorId);
    }

    @PutMapping("/distri/inventory-items/{itemId}")
    public DistriStockProduct updateInventoryItem(@PathVariable Long itemId, @RequestBody DistriStockProduct item, @RequestParam Long distributorId) {
        item.setItemId(itemId); // Assuming setter for inventoryId
        return stockProductService.saveInventoryItem(item, distributorId);
    }

    @DeleteMapping("/distri/inventory-items/{itemId}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long itemId) {
    	stockProductService.deleteInventoryItem(itemId);
        return ResponseEntity.ok().build();
    }
}