package com.example.SupplyChainManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.repository.DistriProductRepository;

@RestController
@RequestMapping("/api/items")
public class ProductSearchApiController {

    @Autowired
    private DistriProductRepository distriProductRepository;

    @GetMapping("/{id}")
    public ResponseEntity<DistriProduct> getItemDetails(@PathVariable Long id) {
        return distriProductRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

