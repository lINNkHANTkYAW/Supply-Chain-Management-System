package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.ItemType;
import com.example.SupplyChainManagement.repository.ItemTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemTypeService {

    @Autowired
    private ItemTypeRepository itemTypeRepository;

    // Get all item types
    public List<ItemType> getAllItemTypes() {
        return itemTypeRepository.findAll();
    }
}