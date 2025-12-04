package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ItemType;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.repository.InventoryItemRepository;
import com.example.SupplyChainManagement.repository.ItemTypeRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private ItemTypeRepository itemTypeRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    public List<InventoryItem> getItemsBySupplierId(Long supplierId) {
        List<InventoryItem> items = inventoryItemRepository.findBySupplier_SupplierId(supplierId);
        return items != null ? items : new ArrayList<>();
    }

    public Optional<InventoryItem> getItemById(Long id) {
        return inventoryItemRepository.findById(id);
    }

    public InventoryItem addItem(Long supplierId, InventoryItem item) {
        Optional<Supplier> supplierOptional = supplierRepository.findById(supplierId);
        if (supplierOptional.isPresent()) {
            Supplier supplier = supplierOptional.get();
            item.setSupplier(supplier);
            // Calculate perUnitCost using BigDecimal division
            if (item.getQuantity() != 0) { // Avoid division by zero
                item.setPerUnitCost(item.getCost().divide(new BigDecimal(item.getQuantity()), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setPerUnitCost(BigDecimal.ZERO); // Handle edge case
            }
            return inventoryItemRepository.save(item);
        }
        return null;
    }

    public InventoryItem updateItem(Long id, InventoryItem updatedItem) {
        Optional<InventoryItem> existingItem = inventoryItemRepository.findById(id);
        if (existingItem.isPresent()) {
            InventoryItem item = existingItem.get();
            item.setName(updatedItem.getName());
            item.setDate(updatedItem.getDate());
            item.setQuantity(updatedItem.getQuantity());
            item.setCost(updatedItem.getCost());
            // Calculate perUnitCost using BigDecimal division
            if (updatedItem.getQuantity() != 0) { // Avoid division by zero
                item.setPerUnitCost(updatedItem.getCost().divide(new BigDecimal(updatedItem.getQuantity()), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setPerUnitCost(BigDecimal.ZERO); // Handle edge case
            }
            item.setItemType(updatedItem.getItemType());
            item.setCategory(updatedItem.getCategory());
            return inventoryItemRepository.save(item);
        }
        return null;
    }

    public void deleteItem(Long id) {
        inventoryItemRepository.deleteById(id);
    }

    public void save(InventoryItem item) {
        inventoryItemRepository.save(item);
    }

    public List<InventoryItem> getAllInventoryItems() {
        return inventoryItemRepository.findAll();
    }

    public InventoryItem findByName(String name) {
        return inventoryItemRepository.findByName(name).orElse(null);
    }

    @Transactional
    public void updateDeliveredItemQuantity(long itemId, int quantity, long supplierId) {
        Optional<InventoryItem> optionalItem = inventoryItemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            InventoryItem item = optionalItem.get();
            if (item.getSupplier().getSupplierId().equals(supplierId)) {
                int newQuantity = item.getQuantity() - quantity;
                if (newQuantity >= 0) {
                    item.setQuantity(newQuantity);
                    inventoryItemRepository.save(item);
                } else {
                    throw new IllegalArgumentException("Quantity cannot be negative");
                }
            } else {
                throw new IllegalArgumentException("Item does not belong to the supplier");
            }
        } else {
            throw new IllegalArgumentException("Item not found");
        }
    }
}