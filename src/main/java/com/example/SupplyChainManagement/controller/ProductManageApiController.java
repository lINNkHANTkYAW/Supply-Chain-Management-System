package com.example.SupplyChainManagement.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import com.example.SupplyChainManagement.service.FileStorageService;
import com.example.SupplyChainManagement.service.ProductService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/products_management")
public class ProductManageApiController {

	private final ProductService productService;
	private final FileStorageService fileStorageService;
	private final DistributorRepository distributorRepository;
	private final CategoryRepository categoryRepository;

	public ProductManageApiController(ProductService productService, FileStorageService fileStorageService,
			DistributorRepository distributorRepository, CategoryRepository categoryRepository) {
		this.productService = productService;
		this.fileStorageService = fileStorageService;
		this.distributorRepository = distributorRepository;
		this.categoryRepository = categoryRepository;
	}

	// Add product (POST)
	@PostMapping
	public ResponseEntity<String> addProduct(@ModelAttribute DistriProduct distriProduct,
			@RequestParam("categoryId") Long categoryId,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile, HttpSession session) {
		try {
			// Ensure user is logged in
			User user = (User) session.getAttribute("loggedInUser");
			if (user == null) {
				return ResponseEntity.status(401).body("Unauthorized: Please log in.");
			}

			// Retrieve distributor
			Optional<Distributor> distributorOpt = distributorRepository.findByUser(user);
			if (distributorOpt.isEmpty()) {
				return ResponseEntity.status(404).body("Distributor not found.");
			}
			distriProduct.setDistributor(distributorOpt.get());

			// Fetch and set category
			Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
			if (categoryOpt.isEmpty()) {
				return ResponseEntity.status(400).body("Invalid Category ID.");
			}
			distriProduct.setCategory(categoryOpt.get());

			// Handle image upload
			if (imageFile != null && !imageFile.isEmpty()) {
				String imagePath = fileStorageService.saveImageFile(imageFile);
				distriProduct.setImage(imagePath);
			}

			// Save product
			productService.addProduct(distriProduct);
			return ResponseEntity.status(302).header("Location", "/testform") // Adjust this to your form page's URL
					.body("Product added successfully");

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error saving product: " + e.getMessage());
		}
	}

	@PutMapping("/edit/{productId}")
	public ResponseEntity<String> editProduct(@PathVariable Long productId,
			@ModelAttribute DistriProduct updatedProduct,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
			@RequestParam("categoryId") Long categoryId, // Retrieve category ID from the form
			HttpSession session) {

		try {
			// Check if user is logged in
			User user = (User) session.getAttribute("loggedInUser");
			if (user == null) {
				return ResponseEntity.status(401).body("Unauthorized: Please log in.");
			}

			// Retrieve distributor
			Optional<Distributor> distributorOpt = distributorRepository.findByUser(user);
			if (distributorOpt.isEmpty()) {
				return ResponseEntity.status(404).body("Distributor not found.");
			}

			// Fetch existing product
			Optional<DistriProduct> existingProductOpt = productService.getProductById(productId);
			if (existingProductOpt.isEmpty()) {
				return ResponseEntity.status(404).body("Product not found.");
			}

			DistriProduct existingProduct = existingProductOpt.get();

			// Update product fields
			existingProduct.setName(updatedProduct.getName());
			existingProduct.setDescription(updatedProduct.getDescription());
			existingProduct.setPrice(updatedProduct.getPrice());
			existingProduct.setStockQuantity(updatedProduct.getStockQuantity());

			// Set the selected category
			
			Optional<Category> categoryOpt = categoryRepository.findById(categoryId); // Fetch category by ID
			if (categoryOpt.isEmpty()) {
				return ResponseEntity.status(404).body("Category not found.");
			}
			existingProduct.setCategory(categoryOpt.get());

			// Optionally update the image
			if (imageFile != null && !imageFile.isEmpty()) {
				String imagePath = fileStorageService.saveImageFile(imageFile);
				existingProduct.setImage(imagePath);
			}

			// Save the updated product
			productService.updateProduct(existingProduct);

			return ResponseEntity.status(302).header("Location", "/testform") // Adjust this to your form page's URL
					.body("Product updated successfully");

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error updating product: " + e.getMessage());
		}
	}

	// Delete product (DELETE)
	@DeleteMapping("/delete/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long productId, HttpSession session) {
		try {
			// Check if user is logged in
			User user = (User) session.getAttribute("loggedInUser");
			if (user == null) {
				return ResponseEntity.status(401).body("Unauthorized: Please log in.");
			}

			// Retrieve distributor
			Optional<Distributor> distributorOpt = distributorRepository.findByUser(user);
			if (distributorOpt.isEmpty()) {
				return ResponseEntity.status(404).body("Distributor not found.");
			}

			// Fetch product by ID
			Optional<DistriProduct> productOpt = productService.getProductById(productId);
			if (productOpt.isEmpty()) {
				return ResponseEntity.status(404).body("Product not found.");
			}

			// Delete product
			productService.deleteProduct(productId);

			return ResponseEntity.status(302).header("Location", "/testform") // Adjust this to your form page's URL
					.body("Product deleted successfully");

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error deleting product: " + e.getMessage());
		}
	}
}
