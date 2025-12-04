package com.example.SupplyChainManagement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.SupplyChainManagement.model.Products;
import com.example.SupplyChainManagement.model.Products;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.repository.CusOrderItemRepository;
import com.example.SupplyChainManagement.repository.ProductsRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ProductsService {
	
    private final ProductsRepository productsRepository;
    private final SupplierRepository supplierRepository;
    private final CusOrderItemRepository orderItemRepository;
    @PersistenceContext
    private EntityManager entityManager;
    
    private final String uploadDir = "src/main/resources/static/img";

    public ProductsService(ProductsRepository productsRepository, CusOrderItemRepository orderItemRepository, SupplierRepository supplierRepository) {
        this.orderItemRepository = orderItemRepository;
        this.supplierRepository = supplierRepository;
        this.productsRepository = productsRepository;
    }

	public List<Products> getAllSupplierProducts() {
        return productsRepository.findAll();
    }
	
	/**
     * Search products by name or description.
     */
    public List<Products> searchProducts(String query) {
        return productsRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    /**
     * Get product by ID.
     */
    public Optional<Products> getProductById(Long productId) {
        return productsRepository.findById(productId);
    }
    
    public Optional<Supplier> getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId);
    }

    /**
     * Save or update a product.
     */
    public Products saveProduct(Products product) {
        return productsRepository.save(product);
    }

    /**
     * Delete a product by ID.
     */
    public boolean deleteProduct(Long productId) {
        if (productsRepository.existsById(productId)) {
            productsRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    /**
     * Get trending products (Most ordered products).
     */
    public List<Products> getTrendingProducts() {
        String query = "SELECT p FROM Products p " +
                       "JOIN CusOrderItem oi ON oi.Products = p " +
                       "GROUP BY p " +
                       "ORDER BY SUM(oi.quantity) DESC";

        return entityManager.createQuery(query, Products.class)
                .setMaxResults(8)
                .getResultList();
    }
    
    public List<Products> getNewArrivals() {
        return productsRepository.findTop8ByOrderByProductIdDesc(); // Newest products based on ID
    }

    

        // Method to update an existing product
        public void updateProduct(Long id, Products updatedProduct) {
            // Fetch the existing product from the database
            Optional<Products> existingProductOptional = productsRepository.findById(id);
            
            if (existingProductOptional.isPresent()) {
                Products existingProduct = existingProductOptional.get();
                
                // Update the fields of the existing product
                existingProduct.setName(updatedProduct.getName());
                existingProduct.setDescription(updatedProduct.getDescription());
                existingProduct.setImages(updatedProduct.getImages());
                existingProduct.setPrice(updatedProduct.getPrice());
                existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
                existingProduct.setRating(updatedProduct.getRating());
                existingProduct.setSupplier(updatedProduct.getSupplier());
                
                // Save the updated product back to the database
                productsRepository.save(existingProduct);
            } else {
                throw new RuntimeException("Product not found with id: " + id);
            }
        }
        
        public List<Products> findByDistributorId(Long supplierId) {
        	return productsRepository.findBySupplier_SupplierId(supplierId);
        }
        
        public String uploadImage(MultipartFile file) throws IOException {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Save the file to the specified directory
            Path destinationPath = Paths.get(uploadDir).resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), destinationPath);

            return destinationPath.toString(); // Return the file path
        }
    
        public Products addProduct(Products Products) {
            // Validate and save the product
            return productsRepository.save(Products);
        }
        
        public void updateProduct(Products updatedProduct) {
            // Ensure the product exists in the database
            if (productsRepository.existsById(updatedProduct.getProductId())) {
                // Save the updated product
                productsRepository.save(updatedProduct);
            } else {
                throw new IllegalArgumentException("Product not found with ID: " + updatedProduct.getProductId());
            }
        }
        
        public List<Products> getProductsByCategoryId(Long categoryId) {
            return productsRepository.findByCategory_CategoryId(categoryId);
        }
        
        public List<Products> getProductsBySupplierId(Long supplierId) {
            return productsRepository.findBySupplier_SupplierId(supplierId);
        }

		public List<Products> findBysupplierId(Long supplierId) {
			// TODO Auto-generated method stub
			return null;
		}
}
