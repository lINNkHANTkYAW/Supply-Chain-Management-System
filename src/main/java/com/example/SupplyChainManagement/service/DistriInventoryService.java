package com.example.SupplyChainManagement.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SupplyChainManagement.dto.DistriDeliveredItemDTO;
import com.example.SupplyChainManagement.dto.DistriInventoryItemDTO;
import com.example.SupplyChainManagement.model.DistriInventory;
import com.example.SupplyChainManagement.repository.DistriInventoryRepository;
import com.example.SupplyChainManagement.repository.DistriOrderItemRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistriInventoryService {
    private static final Logger logger = LoggerFactory.getLogger(DistriInventoryService.class);

    @Autowired
    private DistriOrderItemRepository orderItemsRepository;

    @Autowired
    private DistriInventoryRepository inventoryRepository;

    /*public List<DistriDeliveredItemDTO> getDeliveredItems(Long distributorId) {
        try {
            List<DistriDeliveredItemDTO> items = orderItemsRepository.findCompletedOrderItemsByDistributorId(distributorId);
            logger.info("Fetched {} delivered items for distributorId {}", items.size(), distributorId);
            return items;
        } catch (Exception e) {
            logger.error("Error fetching delivered items for distributorId {}: {}", distributorId, e.getMessage(), e);
            throw e;
        }
    } */
    
    public List<DistriInventory> getDeliveredItems(Long distributorId) {
        // Fetch items from distri_inventory for the given distributorId
        return inventoryRepository.findByDistributorId(distributorId);
    }
    
    /* public List<DistriDeliveredItemDTO> getDeliveredItems(Long distributorId) {
        try {
            List<DistriDeliveredItemDTO> items = orderItemsRepository.findDeliveredItemsByDistributorId(distributorId)
                .stream()
                .map(item -> new DistriDeliveredItemDTO(
                    item.getOrderItemId(),
                    item.getManuProduct().getName(),
                    item.getQuantity(),
                    item.getManuProduct().getCost().doubleValue(),
                    java.sql.Timestamp.valueOf(item.getDistriOrder().getDeliverDate().atStartOfDay()),
                    item.getManuProduct().getCategory().getCategoryName()
                ))
                .collect(Collectors.toList());
            logger.info("Fetched {} delivered items for distributorId {}", items.size(), distributorId);
            return items;
        } catch (Exception e) {
            logger.error("Error fetching delivered items for distributorId {}: {}", distributorId, e.getMessage(), e);
            throw e;
        }
    } */

    /*public List<DistriInventoryItemDTO> getInventoryItems(Long distributorId) {
        try {
            List<DistriInventoryItemDTO> items = inventoryRepository.findByDistributorId(distributorId).stream()
                .map(item -> new DistriInventoryItemDTO(
                    item.getInventoryId(),
                    item.getName(),
                    item.getQuantity(),
                    item.getCost(),
                    item.getCostPerUnit(),
                    item.getAddedDate()
                ))
                .collect(Collectors.toList());
            logger.info("Fetched {} inventory items for distributorId {}", items.size(), distributorId);
            return items;
        } catch (Exception e) {
            logger.error("Error fetching inventory items for distributorId {}: {}", distributorId, e.getMessage(), e);
            throw e;
        }
    }

    public DistriInventoryItemDTO getInventoryItem(Long itemId) {
        DistriInventory item = inventoryRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        return new DistriInventoryItemDTO(
            item.getInventoryId(),
            item.getName(),
            item.getQuantity(),
            item.getCost(),
            item.getCostPerUnit(),
            item.getAddedDate()
        );
    }

    public DistriInventoryItemDTO saveInventoryItem(DistriInventoryItemDTO itemDTO, Long distributorId) {
        DistriInventory item = new DistriInventory();
        item.setInventoryId(itemDTO.getId());
        item.setName(itemDTO.getName());
        item.setQuantity(itemDTO.getQuantity());
        item.setCost(itemDTO.getCost());
        item.setCostPerUnit(itemDTO.getCostPerUnit());
        item.setAddedDate(itemDTO.getAddedDate());
        item.setDistributorId(distributorId);
        DistriInventory savedItem = inventoryRepository.save(item);
        return new DistriInventoryItemDTO(
            savedItem.getInventoryId(),
            savedItem.getName(),
            savedItem.getQuantity(),
            savedItem.getCost(),
            savedItem.getCostPerUnit(),
            savedItem.getAddedDate()
        );
    }

    public void deleteInventoryItem(Long itemId) {
        inventoryRepository.deleteById(itemId);
    } */
    

    public List<DistriInventory> getInventoryItems(Long distributorId) {
        return inventoryRepository.findByDistributorId(distributorId);
    }

    public DistriInventory getInventoryItem(Long itemId) {
        return inventoryRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + itemId));
    }

    public DistriInventory saveInventoryItem(DistriInventory item, Long distributorId) {
        item.setDistributorId(distributorId); // Ensure distributorId is set
        return inventoryRepository.save(item);
    }

    public void deleteInventoryItem(Long itemId) {
        if (!inventoryRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Item not found with ID: " + itemId);
        }
        inventoryRepository.deleteById(itemId);
    }
    
    public DistriInventory addItemToInventory(DistriInventory inventory) {
        return inventoryRepository.save(inventory);
    }

    // Fetch all inventory items for a specific distributor
    public List<DistriInventory> getInventoryByDistributor(Long distributorId) {
        return inventoryRepository.findByDistributorId(distributorId);
    }
    
    public void subtractQuantity(Long inventoryId, int quantityToSubtract) {
        DistriInventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));
        if (inventory.getQuantity() < quantityToSubtract) {
            throw new RuntimeException("Insufficient quantity in inventory");
        }
        inventory.setQuantity(inventory.getQuantity() - quantityToSubtract);
        inventoryRepository.save(inventory);
    }
}