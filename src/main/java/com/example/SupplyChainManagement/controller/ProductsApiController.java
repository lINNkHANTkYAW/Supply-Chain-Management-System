package com.example.SupplyChainManagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Products;
import com.example.SupplyChainManagement.service.ProductService;
import com.example.SupplyChainManagement.service.ProductsService;

public class ProductsApiController {
	
	private final ProductsService productsService;

    public ProductsApiController(ProductsService productsService) {
        this.productsService = productsService;
    }

    /**
     * Get all products.
     * @return List of products.
     */
    @GetMapping
    public ResponseEntity<List<Products>> getAllProducts() {
        List<Products> products = productsService.getAllSupplierProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // No products found
        }
        return ResponseEntity.ok(products);
    }

}
