package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.dto.ManuRawMaterialDTO;
import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.ManuRawMaterial;
import com.example.SupplyChainManagement.repository.ManuRawMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManuRawMaterialService {

    @Autowired
    private ManuRawMaterialRepository manuRawMaterialRepository;

    public List<ManuRawMaterial> getRawMaterials(Long manufacturerId) {
        return manuRawMaterialRepository.findByManufacturer_ManufacturerId(manufacturerId);
    }
    
    public List<ManuRawMaterialDTO> getAllRawMaterials() {
        return manuRawMaterialRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ManuRawMaterialDTO mapToDTO(ManuRawMaterial item) {
        ManuRawMaterialDTO dto = new ManuRawMaterialDTO();
        dto.setRawMaterialMid(item.getRawMaterialMid());
        dto.setName(item.getName());
        dto.setCategoryName(item.getCategory().getCategoryName());
        dto.setQtyOnHand(item.getQtyOnHand());
        dto.setUnitCost(item.getUnitCost());
        dto.setTotalCost(item.getUnitCost().multiply(BigDecimal.valueOf(item.getQtyOnHand()))); // Calculate total cost
        dto.setAddedDate(item.getAddedDate());
        return dto;
    }

    public ManuRawMaterial addRawMaterial(ManuRawMaterial rawMaterial) {
        return manuRawMaterialRepository.save(rawMaterial);
    }

    public ManuRawMaterial updateRawMaterial(Long id, ManuRawMaterial updatedRawMaterial) {
        ManuRawMaterial rawMaterial = manuRawMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Raw Material not found"));
        rawMaterial.setName(updatedRawMaterial.getName());
        rawMaterial.setQtyOnHand(updatedRawMaterial.getQtyOnHand());
        rawMaterial.setUnitCost(updatedRawMaterial.getUnitCost());
        rawMaterial.setUnitPrice(updatedRawMaterial.getUnitPrice());
        return manuRawMaterialRepository.save(rawMaterial);
    }

    public void deleteRawMaterial(Long id) {
        manuRawMaterialRepository.deleteById(id);
    }
    
    public List<ManuRawMaterial> getAllDeliveredItems() {
        return manuRawMaterialRepository.findAll();
    }
    
    public String updateQuantity(Long itemId, int newQuantity, Long manufacturerId) {
        ManuRawMaterial item = manuRawMaterialRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Check if the item belongs to the specified manufacturer
        if (!item.getManufacturer().getManufacturerId().equals(manufacturerId)) {
            return "Error: You do not have permission to update this item.";
        }

        if (newQuantity < 0 || newQuantity > item.getQtyOnHand()) {
            return "Error: Quantity must be between 0 and " + item.getQtyOnHand();
        }

        item.setQtyOnHand(newQuantity);
        manuRawMaterialRepository.save(item);
        return "Quantity updated successfully!";
    }
    
    public void deleteDeliveredItem(Long itemId) {
        manuRawMaterialRepository.deleteById(itemId);
    }
    
    public List<ManuRawMaterial> getDeliveredItemsByManufacturer(Long manufacturerId) {
        return manuRawMaterialRepository.findByManufacturer_ManufacturerId(manufacturerId);
    }
    
    public String deleteDeliveredItem(Long itemId, Long manufacturerId) {
        ManuRawMaterial item = manuRawMaterialRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Check if the item belongs to the specified manufacturer
        if (!item.getManufacturer().getManufacturerId().equals(manufacturerId)) {
            return "Error: You do not have permission to delete this item.";
        }

        manuRawMaterialRepository.deleteById(itemId);
        return "Item deleted successfully!";
    }
    
    public List<ManuRawMaterial> getMaterialsForManufacturer(Long manufacturerId) {
        return manuRawMaterialRepository.findByManufacturer_ManufacturerId(manufacturerId);
    }
    
    public String updateQuantity(Long itemId, int newQuantity) {
        ManuRawMaterial item = manuRawMaterialRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (newQuantity < 0 || newQuantity > item.getQtyOnHand()) {
            return "Error: Quantity must be between 0 and " + item.getQtyOnHand();
        }

        item.setQtyOnHand(newQuantity);
        manuRawMaterialRepository.save(item);
        return "Quantity updated successfully!";
    }
}