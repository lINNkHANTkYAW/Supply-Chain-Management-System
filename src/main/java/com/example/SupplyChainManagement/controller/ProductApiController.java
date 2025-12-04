package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.Products;
import com.example.SupplyChainManagement.service.DistributorService;
import com.example.SupplyChainManagement.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Get all products.
     * @return List of products.
     */
    @GetMapping
    public ResponseEntity<List<DistriProduct>> getAllProducts() {
        List<DistriProduct> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // No products found
        }
        return ResponseEntity.ok(products);
    }
    

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<DistriProduct> products = productService.getProductsByCategoryId(categoryId);
        if (products.isEmpty()) return ResponseEntity.noContent().build();

        List<Map<String, Object>> productList = products.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }
    
    @GetMapping("/distributor/{distributorId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsByDistributor(@PathVariable Long distributorId) {
        List<DistriProduct> products = productService.getProductsByDistributorId(distributorId);
        if (products.isEmpty()) return ResponseEntity.noContent().build();

        List<Map<String, Object>> productList = products.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }
    
    /**
     * Search products based on the query string.
     * @param query Search term.
     * @return List of matching products.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchProducts(@RequestParam String query) {
        List<DistriProduct> products = productService.searchProducts(query);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> productResults = products.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getProductId());
            map.put("name", product.getName());
            map.put("price", product.getPrice());
            map.put("rating", product.getRating());
            map.put("image", product.getImage() != null ? product.getImage() : "/img/default-product.jpg");

            // Include distributor details
            if (product.getDistributor() != null) {
                map.put("distributorId", product.getDistributor().getDistributorId());
                map.put("distributorName", product.getDistributor().getCompanyName());
            } else {
                map.put("distributorId", null);
                map.put("distributorName", "Unknown Distributor");
            }

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(productResults);
    }


    /**
     * Get product details by product ID.
     * @param productId Product ID.
     * @return Product details wrapped in ResponseEntity.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<DistriProduct> getProductDetails(@PathVariable Long productId) {
        Optional<DistriProduct> product = productService.getProductById(productId);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Return 404 if not found
    }

    /**
     *Get trending products.
     *@return List of trending products.
     
    @GetMapping("/trending")
    public ResponseEntity<List<DistriProduct>> getTrendingProducts() {
        List<DistriProduct> trendingProducts = productService.getTrendingProducts();
        if (trendingProducts.isEmpty()) {
            return ResponseEntity.noContent().build(); // No trending products found
        }
        return ResponseEntity.ok(trendingProducts);
    } 

    /**
     * Add a new product.
     * @param product The product to be added.
     * @return Created product response.
     */
    /* @PostMapping
    public ResponseEntity<DistriProduct> addProduct(@RequestBody DistriProduct product) {
        DistriProduct savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    } */

    /**
     * Update an existing product.
     * @param productId ID of the product to update.
     * @param updatedProduct Updated product data.
     * @return Response with updated product or 404 if not found.
     */
    /* @PutMapping("/{productId}")
    public ResponseEntity<DistriProduct> updateProduct(@PathVariable Long productId, @RequestBody DistriProduct updatedProduct) {
        Optional<DistriProduct> existingProduct = productService.getProductById(productId);
        if (existingProduct.isPresent()) {
            updatedProduct.setProductId(productId);
            DistriProduct savedProduct = productService.saveProduct(updatedProduct);
            return ResponseEntity.ok(savedProduct);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    } */

    /**
     * Delete a product by ID.
     * @param productId ID of the product to delete.
     * @return Response indicating success or failure.
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        if (productService.deleteProduct(productId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingProducts() {
        List<DistriProduct> trendingProducts = productService.getTrendingProducts();
        if (trendingProducts.isEmpty()) {
            return ResponseEntity.noContent().build(); // No trending products found
        }
        List<Map<String, Object>> response = trendingProducts.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/brands")
    public ResponseEntity<List<Map<String, Object>>> getNewArrivals() {
        List<DistriProduct> newArrivals = productService.getNewArrivals();
        if (newArrivals.isEmpty()) {
            return ResponseEntity.noContent().build(); // No new arrivals found
        }
        List<Map<String, Object>> response = newArrivals.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> convertToResponse(DistriProduct product) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", product.getProductId());
        productData.put("name", product.getName());
        productData.put("price", product.getPrice());
        productData.put("rating", product.getRating());
        productData.put("description", product.getDescription());
        productData.put("stockQuantity", product.getStockQuantity());
        productData.put("image", product.getImage());
        productData.put("distributorId", product.getDistributor().getDistributorId());
        productData.put("companyName", product.getDistributor().getCompanyName());
        productData.put("categoryName", product.getCategory().getCategoryName());
        productData.put("distributorRating", product.getDistributor().getRating());

        // Use the image path directly
        String imagePath = product.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            productData.put("image", imagePath); // Just the path
        } else {
            productData.put("image", "/img/default-product.jpg"); // Default image if none is provided
        }

        return productData;
    }
    
    /* @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<Optional<Distributor>> getSellerProfile(@PathVariable Long sellerId) {
        DistributorService distributorService = null;
		Optional<Distributor> distributor = distributorService.getDistributorById(sellerId);

        if (distributor == null) {
            return ResponseEntity.notFound().build();
        }

        

        return ResponseEntity.ok(distributor);
    } */

    @PostMapping
    public ResponseEntity<Void> addProduct(@ModelAttribute DistriProduct product) {
        productService.saveProduct(product);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @ModelAttribute DistriProduct product) {
        productService.updateProduct(id, product);
        return ResponseEntity.ok().build();
    }
    
}

