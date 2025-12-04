package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.dto.ItemDTO;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.repository.CusOrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CusOrderItemService {
    private final CusOrderItemRepository cusOrderItemRepository;

    public CusOrderItemService(CusOrderItemRepository cusOrderItemRepository) {
        this.cusOrderItemRepository = cusOrderItemRepository;
    }

    public List<ItemDTO> getItemsByCategoryId(Long categoryId) {
        return cusOrderItemRepository.findItemsByCategoryId(categoryId)
                .stream()
                .map(item -> new ItemDTO(
                        item.getItemId(),
                        item.getProductName(),
                        item.getCategoryName(),
                        item.getImageUrl(),
                        item.getQuantity(),
                        item.getRating(),
                        item.getDistributorName(),
                        item.getPrice(),
                        item.getDescription()
                        
                ))
                .toList();
    }

    public ItemDTO getItemDetails(Long itemId) {
        ItemDTO item = cusOrderItemRepository.findItemDetailsById(itemId);

        if (item == null) {
            throw new RuntimeException("Item not found");
        }

        return new ItemDTO(
        		item.getItemId(),
                item.getProductName(),
                item.getCategoryName(),
                item.getImageUrl(),
                item.getQuantity(),
                item.getRating(),
                item.getDistributorName(),
                item.getPrice(),
                item.getDescription()
        );
    }

}
