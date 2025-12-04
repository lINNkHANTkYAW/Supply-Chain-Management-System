package com.example.SupplyChainManagement.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SupplyChainManagement.model.DistriStockProduct;

import com.example.SupplyChainManagement.repository.DistriOrderItemRepository;
import com.example.SupplyChainManagement.repository.DistriStockProductRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistriStockProductService {
    private static final Logger logger = LoggerFactory.getLogger(DistriStockProductService.class);

    @Autowired
    private DistriOrderItemRepository orderItemsRepository;

    @Autowired
    private DistriStockProductRepository stockProductRepository;

    

    public List<DistriStockProduct> getInventoryItems(Long distributorId) {
        return stockProductRepository.findByDistributorId(distributorId);
    }

    public DistriStockProduct getInventoryItem(Long itemId) {
        return stockProductRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + itemId));
    }

    public DistriStockProduct saveInventoryItem(DistriStockProduct item, Long distributorId) {
        item.setDistributorId(distributorId); // Ensure distributorId is set
        return stockProductRepository.save(item);
    }

    public void deleteInventoryItem(Long itemId) {
        if (!stockProductRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Item not found with ID: " + itemId);
        }
        stockProductRepository.deleteById(itemId);
    }
    
    public DistriStockProduct addItemToInventory(DistriStockProduct inventory) {
        return stockProductRepository.save(inventory);
    }

    // Fetch all inventory items for a specific distributor
    public List<DistriStockProduct> getInventoryByDistributor(Long distributorId) {
        return stockProductRepository.findByDistributorId(distributorId);
    }
}