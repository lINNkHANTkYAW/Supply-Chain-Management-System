package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.dto.ManuInventoryItemUpdateDTO;
import com.example.SupplyChainManagement.dto.ManuProductDTO;
import com.example.SupplyChainManagement.dto.ManuProductUpdateDTO;
import com.example.SupplyChainManagement.dto.NewManuProductDTO;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.ManuInventoryItem;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.ManuStockProduct;
import com.example.SupplyChainManagement.model.Products;
import com.example.SupplyChainManagement.service.DistributorService;
import com.example.SupplyChainManagement.service.ManuInventoryService;
import com.example.SupplyChainManagement.service.ManuProductService;
import com.example.SupplyChainManagement.service.ManuStockProductService;

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
@RequestMapping("/api/manu/products")
public class ManuProductApiController {

    private final ManuProductService manuProductService;
    private final ManuStockProductService manuStockProductService;
    private final ManuInventoryService manuInventoryService;

    public ManuProductApiController(ManuProductService manuProductService, ManuStockProductService manuStockProductService, ManuInventoryService manuInventoryService) {
        this.manuProductService = manuProductService;
        this.manuStockProductService = manuStockProductService;
        this.manuInventoryService = manuInventoryService;
    }

    /**
     * Get all products.
     * @return List of products.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ManuProduct>> getAllProducts() {
        List<ManuProduct> products = manuProductService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // No products found
        }
        return ResponseEntity.ok(products);
    }
    

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ManuProduct> products = manuProductService.getProductsByCategoryId(categoryId);
        if (products.isEmpty()) return ResponseEntity.noContent().build();

        List<Map<String, Object>> productList = products.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }
    
    @GetMapping("/manufacturer/{manufacturerId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsByManufacturer(@PathVariable Long manufacturerId) {
        List<ManuProduct> products = manuProductService.getProductsByManufacturerId(manufacturerId);
        if (products.isEmpty()) return ResponseEntity.noContent().build();

        List<Map<String, Object>> productList = products.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }
    
    /**
     * Get product details by product ID.
     * @param productId Product ID.
     * @return Product details wrapped in ResponseEntity.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ManuProduct> getProductDetails(@PathVariable Long productId) {
        Optional<ManuProduct> product = manuProductService.getProductById(productId);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Return 404 if not found
    }
    
    /**
     * Search products based on the query string.
     * @param query Search term.
     * @return List of matching products.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchProducts(@RequestParam String query) {
        List<ManuProduct> products = manuProductService.searchProducts(query);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> productResults = products.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getProductMid());
            map.put("name", product.getName());
            map.put("price", product.getPrice());
            map.put("description", product.getDescription());
            // map.put("rating", product.getRating());
            map.put("image", product.getImage() != null ? product.getImage() : "/img/default-product.jpg");

            // Include distributor details
            if (product.getManufacturer() != null) {
                map.put("manufacturerId", product.getManufacturer().getManufacturerId());
                map.put("companyName", product.getManufacturer().getCompanyName());
            } else {
                map.put("manufacturerId", null);
                map.put("companyName", "Unknown Manufacturer");
            }

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(productResults);
    }


  

    /**
     *Get trending products.
     *@return List of trending products.
     
    @GetMapping("/trending")
    public ResponseEntity<List<DistriProduct>> getTrendingProducts() {
        List<DistriProduct> trendingProducts = manuProductService.getTrendingProducts();
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
        DistriProduct savedProduct = manuProductService.saveProduct(product);
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
        Optional<DistriProduct> existingProduct = manuProductService.getProductById(productId);
        if (existingProduct.isPresent()) {
            updatedProduct.setProductId(productId);
            DistriProduct savedProduct = manuProductService.saveProduct(updatedProduct);
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
    /* @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        if (manuProductService.deleteProduct(productId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    } */
    
    /* @GetMapping("/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingProducts() {
        List<DistriProduct> trendingProducts = manuProductService.getTrendingProducts();
        if (trendingProducts.isEmpty()) {
            return ResponseEntity.noContent().build(); // No trending products found
        }
        List<Map<String, Object>> response = trendingProducts.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/brands")
    public ResponseEntity<List<Map<String, Object>>> getNewArrivals() {
        List<DistriProduct> newArrivals = manuProductService.getNewArrivals();
        if (newArrivals.isEmpty()) {
            return ResponseEntity.noContent().build(); // No new arrivals found
        }
        List<Map<String, Object>> response = newArrivals.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    } */

    private Map<String, Object> convertToResponse(ManuProduct product) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", product.getProductMid());
        productData.put("name", product.getName());
        productData.put("price", product.getPrice());
        productData.put("description", product.getDescription());
        productData.put("stockQuantity", product.getStockQuantity());
        productData.put("image", product.getImage());
        productData.put("manufacturerId", product.getManufacturer().getManufacturerId());
        productData.put("companyName", product.getManufacturer().getCompanyName());
        productData.put("categoryName", product.getCategory().getCategoryName());

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

    /* @PostMapping
    public ResponseEntity<Void> addProduct(@ModelAttribute DistriProduct product) {
        manuProductService.saveProduct(product);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @ModelAttribute DistriProduct product) {
        manuProductService.updateProduct(id, product);
        return ResponseEntity.ok().build();
    } */
    
    @GetMapping
    public ResponseEntity<List<ManuInventoryItem>> getItemsByManufacturerId(@RequestParam Long manufacturerId) {
        List<ManuInventoryItem> items = manuInventoryService.getItemsByManufacturerId(manufacturerId);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/new/{itemId}")
    public ResponseEntity<NewManuProductDTO> getInventoryItem(@PathVariable Long itemId) {
        try {
            System.out.println("Received request to fetch item with ID: " + itemId);
            NewManuProductDTO item = manuStockProductService.getInventoryItem(itemId);
            System.out.println("Item fetched: " + item);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            System.err.println("Error fetching item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Add a new inventory item
    @PostMapping
    public ResponseEntity<ManuInventoryItem> addInventoryItem(@RequestBody ManuInventoryItem itemDTO, @RequestParam Long manufacturerId) {
        try {
            System.out.println("Received request to add item: " + itemDTO);
            System.out.println("Manufacturer ID: " + manufacturerId);
            ManuInventoryItem savedItem = manuInventoryService.addInventoryItem(itemDTO, manufacturerId);
            System.out.println("Item saved: " + savedItem);
            return ResponseEntity.ok(savedItem);
        } catch (Exception e) {
            System.err.println("Error adding item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Update an inventory item
    /* @PutMapping("/{itemId}")
    public ResponseEntity<NewManuProductDTO> updateInventoryItem(@PathVariable Long itemId, @RequestBody NewManuProductDTO itemDTO) {
    	NewManuProductDTO updatedItem = manuProductService.updateInventoryItem(itemId, itemDTO);
        return ResponseEntity.ok(updatedItem);
    } */
    
    /* @PutMapping("/update/{id}")
    public ResponseEntity<ManuInventoryItem> updateManuStockProduct(
            @PathVariable("id") Long id,
            @RequestBody ManuInventoryItem updateDTO) {
        try {
        	ManuInventoryItem updatedProduct = manuInventoryService.updateManufacturerItem(id, updateDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    } */
    
    @PutMapping("/update/{id}")
    public ResponseEntity<ManuInventoryItem> updateManuStockProduct(
            @PathVariable("id") Long id,
            @RequestBody ManuInventoryItemUpdateDTO updateDTO) {
        try {
            ManuInventoryItem item = manuInventoryService.updateManufacturerItem(id, updateDTO);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete an inventory item
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long itemId) {
        manuInventoryService.deleteManufacturerItem(itemId);
        return ResponseEntity.noContent().build();
    }
}


