package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.ManuOrderItem;
import com.example.SupplyChainManagement.repository.ManuOrderItemRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManuOrderItemService {

    @Autowired
    private ManuOrderItemRepository manuOrderItemRepository;

    public ManuOrderItem addOrderItem(ManuOrderItem orderItem) {
        return manuOrderItemRepository.save(orderItem);
    }
    
    public List<ManuOrderItem> getDeliveredItems() {
    	List<ManuOrderItem> items = manuOrderItemRepository.findDeliveredItems();
        System.out.println("Delivered items count: " + items.size()); // Debug log
        return items;
    }

    // Add methods for update and delete if needed
    public ManuOrderItem updateDeliveredItem(Long id, ManuOrderItem updatedItem) {
        ManuOrderItem item = manuOrderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivered item not found"));
        item.setSupplierRawMaterial(updatedItem.getSupplierRawMaterial());;
        // item.setCategory(updatedItem.getCategory());
        // item.setItemType(updatedItem.getItemType());
        item.setQuantity(updatedItem.getQuantity());
        // item.setCost(updatedItem.getCost());
        // item.setArrivalDate(updatedItem.getArrivalDate()); // Use String for date as per your model
        return manuOrderItemRepository.save(item);
    }

    public void deleteDeliveredItem(Long id) {
        manuOrderItemRepository.deleteById(id);
    }
}