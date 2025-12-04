package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Products;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.repository.CusOrderItemRepository;
import com.example.SupplyChainManagement.repository.DistriProductRepository;
import com.example.SupplyChainManagement.repository.ProductsRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final DistriProductRepository productRepository;
    private final ProductsRepository productsRepository;
    private final SupplierRepository supplierRepository;
    private final CusOrderItemRepository orderItemRepository;
    @PersistenceContext
    private EntityManager entityManager;
    
    private final String uploadDir = "src/main/resources/static/img";

    public ProductService(DistriProductRepository productRepository, CusOrderItemRepository orderItemRepository, SupplierRepository supplierRepository, ProductsRepository productsRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.supplierRepository = supplierRepository;
        this.productsRepository = productsRepository;
    }

    /**
     * Get all products.
     */
    public List<DistriProduct> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<DistriProduct> getProductsBySeller(Long sellerId) {
	    return productRepository.findByUser_UserId(sellerId);
	}

    /**
     * Search products by name or description.
     */
    public List<DistriProduct> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    /**
     * Get product by ID.
     */
    public Optional<DistriProduct> getProductById(Long productId) {
        return productRepository.findById(productId);
    }
    
    public Optional<Supplier> getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId);
    }

    /**
     * Save or update a product.
     */
    public DistriProduct saveProduct(DistriProduct product) {
        return productRepository.save(product);
    }

    /**
     * Delete a product by ID.
     */
    public boolean deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    /**
     * Get trending products (Most ordered products).
     */
    public List<DistriProduct> getTrendingProducts() {
        String query = "SELECT p FROM DistriProduct p " +
                       "JOIN CusOrderItem oi ON oi.distriProduct = p " +
                       "GROUP BY p " +
                       "ORDER BY SUM(oi.quantity) DESC";

        return entityManager.createQuery(query, DistriProduct.class)
                .setMaxResults(8)
                .getResultList();
    }
    
    public List<DistriProduct> getNewArrivals() {
        return productRepository.findTop8ByOrderByProductIdDesc(); // Newest products based on ID
    }

    

        // Method to update an existing product
        public void updateProduct(Long id, DistriProduct updatedProduct) {
            // Fetch the existing product from the database
            Optional<DistriProduct> existingProductOptional = productRepository.findById(id);
            
            if (existingProductOptional.isPresent()) {
                DistriProduct existingProduct = existingProductOptional.get();
                
                // Update the fields of the existing product
                existingProduct.setName(updatedProduct.getName());
                existingProduct.setDescription(updatedProduct.getDescription());
                existingProduct.setImage(updatedProduct.getImage());
                existingProduct.setPrice(updatedProduct.getPrice());
                existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
                existingProduct.setRating(updatedProduct.getRating());
                existingProduct.setDistributor(updatedProduct.getDistributor());
                
                // Save the updated product back to the database
                productRepository.save(existingProduct);
            } else {
                throw new RuntimeException("Product not found with id: " + id);
            }
        }
        
        public List<DistriProduct> findByDistributorId(Long distributorId) {
        	return productRepository.findByDistributor_DistributorId(distributorId);
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
    
        public DistriProduct addProduct(DistriProduct distriProduct) {
            // Validate and save the product
            return productRepository.save(distriProduct);
        }
        
        public void updateProduct(DistriProduct updatedProduct) {
            // Ensure the product exists in the database
            if (productRepository.existsById(updatedProduct.getProductId())) {
                // Save the updated product
                productRepository.save(updatedProduct);
            } else {
                throw new IllegalArgumentException("Product not found with ID: " + updatedProduct.getProductId());
            }
        }
        
        public List<DistriProduct> getProductsByCategoryId(Long categoryId) {
            return productRepository.findByCategory_CategoryId(categoryId);
        }
        
        public List<DistriProduct> getProductsByDistributorId(Long distributorId) {
            return productRepository.findByDistributor_DistributorId(distributorId);
        }

		public List<DistriProduct> findBysupplierId(Long supplierId) {
			// TODO Auto-generated method stub
			return null;
		}
}
