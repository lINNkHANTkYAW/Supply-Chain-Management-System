/* package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.service.DistriProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distriproducts")
public class DistriProductApiController {

    @Autowired
    private DistriProductService productService;

    @GetMapping
    public ResponseEntity<List<DistriProduct>> getAllProducts(HttpSession session) {
        Long distributorId = (Long) session.getAttribute("distributorId");
        List<DistriProduct> products = productService.getAllProductsByDistributor(distributorId);
        return ResponseEntity.ok(products);
    }

    /* @GetMapping("/{productId}")
    public ResponseEntity<DistriProduct> getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId)
                .map(ResponseEntity::ok)
                .orElse(Response Entity.notFound().build());
    } */

    /* @PostMapping
    public ResponseEntity<DistriProduct> addProduct(@RequestBody DistriProduct product, HttpSession session) {
        Long distributorId = (Long) session.getAttribute("distributorId");
        product.setDistributor(new Distributor()); // Assuming Distributor constructor accepts ID
        DistriProduct savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<DistriProduct> updateProduct(@PathVariable Long productId, @RequestBody DistriProduct product) {
        product.setProductId(productId);
        DistriProduct updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}  */