package com.example.SupplyChainManagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SupplyChainManagement.dto.ManuInventoryItemUpdateDTO;
import com.example.SupplyChainManagement.dto.NewManuProductDTO;
import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ManuInventoryItem;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.ManuInventoryItemRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;

@Service
public class ManuInventoryService {

    @Autowired
    private ManuInventoryItemRepository inventoryItemRepository;
    
    @Autowired
    private ManufacturerRepository manuRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<ManuInventoryItem> getManufacturerInventory() {
        return inventoryItemRepository.findAll(); // Fetch all items, as they are all manufacturer inventory
    }
    
    public List<ManuInventoryItem> getItemsByManufacturerId(Long manufacturerId) {
        List<ManuInventoryItem> items = inventoryItemRepository.findByManufacturer_ManufacturerId(manufacturerId);
        return items != null ? items : new ArrayList<>();
    }

    public ManuInventoryItem addManufacturerItem(ManuInventoryItem item) {
        // Set default addedDate if not provided (current date)
        if (item.getAddedDate() == null) {
            item.setAddedDate(LocalDate.now());
        }
        // Ensure category is set from categoryId if provided
        if (item.getCategory() != null && item.getCategory().getCategoryId() != null) {
            Category category = categoryRepository.findById(item.getCategory().getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + item.getCategory().getCategoryId()));
            item.setCategory(category);
        }
        return inventoryItemRepository.save(item);
    }
    
    public ManuInventoryItem addInventoryItem(ManuInventoryItem itemDTO, Long manufacturerId) {
    	ManuInventoryItem item = new ManuInventoryItem();
        item.setName(itemDTO.getName());
        // item.setDescription(itemDTO.getDescription());
        // item.setPrice(itemDTO.getPrice());
        item.setCost(itemDTO.getCost());
        item.setQuantity(itemDTO.getQuantity());
        item.setCategory(categoryRepository.findById(itemDTO.getCategory().getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));
        
        Manufacturer manufacturer = manuRepository.findById(manufacturerId)
                .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        item.setManufacturer(manufacturer);
        item.setAddedDate(itemDTO.getAddedDate());
        ManuInventoryItem savedItem = inventoryItemRepository.save(item);
        return savedItem;
    }

    public ManuInventoryItem updateManufacturerItem(Long id, ManuInventoryItemUpdateDTO updatedItem) {
        ManuInventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manufacturer inventory item not found with ID: " + id));
        item.setName(updatedItem.getName());
        item.setCategory(categoryRepository.findById(updatedItem.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found")));
        item.setQuantity(updatedItem.getQuantity());
        item.setCost(updatedItem.getCost());
        item.setPerUnitCost(updatedItem.getPerUnitCost());
        item.setAddedDate(updatedItem.getAddedDate() != null ? updatedItem.getAddedDate() : item.getAddedDate());
        return inventoryItemRepository.save(item);
    }
    
    

    public void deleteManufacturerItem(Long id) {
        ManuInventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manufacturer inventory item not found with ID: " + id));
        inventoryItemRepository.delete(item);
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
    
    public ManuInventoryItem findByName(String name) {
        return inventoryItemRepository.findByName(name).orElse(null);
    }
}