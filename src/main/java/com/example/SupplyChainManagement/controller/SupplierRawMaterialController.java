package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.service.CategoryService;
import com.example.SupplyChainManagement.service.SupplierService;
import com.example.SupplyChainManagement.service.UserService;
import com.example.SupplyChainManagement.service.InventoryService;
import com.example.SupplyChainManagement.service.SupplierMaterialService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/supplier-raw-materials")
public class SupplierRawMaterialController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierRawMaterialController.class);

    @Autowired
    private SupplierMaterialService supplierRawMaterialService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping
    public String showSupplierRawMaterials(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/userRegistration?form=login";
        }
        Long userId = user.getUserId();
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        if (supplierOptional.isEmpty()) {
            return "redirect:/supplier-dashboard";
        }

        Supplier supplier = supplierOptional.get();
        Long supplierId = supplier.getSupplierId();

        List<InventoryItem> inventoryItems = inventoryService.getAllInventoryItems();
        Set<String> existingRawMaterialNames = supplierRawMaterialService.getExistingRawMaterialNamesBySupplierId(supplierId);
     // Existing code in showSupplierRawMaterials()
        List<InventoryItem> availableInventoryItems = inventoryItems.stream()
            .filter(item -> item.getSupplier() != null && item.getSupplier().getSupplierId().equals(supplierId)) // FIXED supplier ID check
            .filter(item -> !existingRawMaterialNames.contains(item.getName()))
            .collect(Collectors.toList());

        List<SupplierRawMaterial> supplierRawMaterials = supplierRawMaterialService.getSupplierRawMaterialsBySupplierId(supplierId);
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("user", user);
        model.addAttribute("supplierRawMaterials", supplierRawMaterials);
        model.addAttribute("categories", categories);
        model.addAttribute("inventoryItems", availableInventoryItems);
        model.addAttribute("supplierRawMaterial", new SupplierRawMaterial());
        model.addAttribute("isEditMode", false);
        model.addAttribute("supplier", supplier);
        model.addAttribute("loggedInSupplier", supplierOptional);
        return "addRawMaterial";
    }
    // private static final String UPLOAD_DIR = "src/main/resources/static/img/";
    @PostMapping("/save")
    public String saveSupplierRawMaterial(@ModelAttribute SupplierRawMaterial supplierRawMaterial,
                                         @RequestParam("imageFile") MultipartFile imageFile,
                                         HttpSession session, Model model) {
        Supplier supplier = getLoggedInSupplier(session);
        if (supplier == null) {
            return "redirect:/userRegistration?form=login";
        }

        supplierRawMaterial.setSupplier(supplier);

        try {
            String uploadDir = "uploads/";
            SupplierRawMaterial existingRawMaterial = null;

            if (supplierRawMaterial.getRawMaterialSid() != null) {
                existingRawMaterial = supplierRawMaterialService.getSupplierRawMaterialById(supplierRawMaterial.getRawMaterialSid());
                if (existingRawMaterial == null) {
                    model.addAttribute("error", "Raw material not found.");
                    return "addRawMaterial";
                }

                supplierRawMaterial.setName(existingRawMaterial.getName());
                supplierRawMaterial.setCategory(existingRawMaterial.getCategory());
                
                if (existingRawMaterial.getCategory().getCategoryId() != null) {
                    Optional<Category> category = Optional.ofNullable(categoryService.getCategoryBy_CategoryId(existingRawMaterial.getCategory().getCategoryId()));
                    category.ifPresent(supplierRawMaterial::setCategory);
                }
            }

            if (supplierRawMaterial.getDescription() == null || supplierRawMaterial.getDescription().trim().isEmpty()) {
                model.addAttribute("error", "Description cannot be empty.");
                return "addRawMaterial";
            }

            if (!imageFile.isEmpty()) {
                try {
                    String fileName = supplier.getUser().getUserId() + "_" + imageFile.getOriginalFilename().replaceAll("\\s+", "");
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, imageFile.getBytes());
                    supplierRawMaterial.setImage(fileName);
                    System.out.println("Image saved to: " + filePath.toAbsolutePath());
                } catch (IOException e) {
                    model.addAttribute("error", "Image upload failed: " + e.getMessage());
                    System.err.println("Image upload failed: " + e.getMessage());
                    return "addRawMaterial";
                }
            } else if (existingRawMaterial != null) {
                supplierRawMaterial.setImage(existingRawMaterial.getImage());
            }

            supplierRawMaterialService.saveSupplierRawMaterial(supplierRawMaterial);
            System.out.println("Raw material saved: " + supplierRawMaterial.getName() + ", image: " + supplierRawMaterial.getImage());
            return "redirect:/supplier-raw-materials";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save supplier raw material: " + e.getMessage());
            System.err.println("Failed to save raw material: " + e.getMessage());
            return "addRawMaterial";
        }
    }
    @GetMapping("/available-inventory")
    @ResponseBody
    public List<InventoryItem> getAvailableInventoryItems(@RequestParam Long supplierId, Model model) {
        List<InventoryItem> allInventoryItems = inventoryService.getAllInventoryItems();
        Set<String> existingRawMaterialNames = supplierRawMaterialService.getExistingRawMaterialNamesBySupplierId(supplierId);
        return allInventoryItems.stream()
                .filter(item -> !existingRawMaterialNames.contains(item.getName()))
                .collect(Collectors.toList());
    }
   /* @GetMapping("/edit/{rawMaterialSid}")
    public String showEditSupplierRawMaterialForm(@PathVariable Long rawMaterialSid, Model model, HttpSession session) {
        Supplier supplier = getLoggedInSupplier(session);
        if (supplier == null) {
            return "redirect:/login";
        }

        SupplierRawMaterial supplierRawMaterial = supplierRawMaterialService.getSupplierRawMaterialById(rawMaterialSid);
        if (supplierRawMaterial == null) {
            logger.warn("Raw material not found for rawMaterialSid: {}", rawMaterialSid);
            return "redirect:/supplier-raw-materials";
        }

        if (!supplierRawMaterial.getSupplierRid().equals(supplier.getSupplierId())) {
            logger.warn("Unauthorized access: rawMaterialSid {} does not belong to supplier {}", rawMaterialSid, supplier.getSupplierId());
            return "redirect:/supplier-raw-materials";
        }

        // Ensure the category is loaded or fetched based on categoryRid
        if (supplierRawMaterial.getCategoryRid() != null) {
            Optional<Category> category = categoryService.findCategoryById(supplierRawMaterial.getCategoryRid());
            category.ifPresent(supplierRawMaterial::setCategory); // Set the category if it exists
        }

        logger.debug("SupplierRawMaterial for edit with category: {}", supplierRawMaterial);

        List<Category> categories = categoryService.findAllCategories();
        List<InventoryItem> inventoryItems = inventoryService.getAllInventoryItems();

        model.addAttribute("supplierRawMaterial", supplierRawMaterial);
        model.addAttribute("categories", categories);
        model.addAttribute("inventoryItems", inventoryItems);
        model.addAttribute("isEditMode", true);
        model.addAttribute("loggedInSupplier", supplier);
        return "addRawMaterial";
    }*/
    @GetMapping("/delete/{rawMaterialSid}")
    public String deleteSupplierRawMaterial(@PathVariable Long rawMaterialSid, HttpSession session) {
        Supplier supplier = getLoggedInSupplier(session);
        if (supplier == null) {
        	return "redirect:/userRegistration?form=login";
        }

        SupplierRawMaterial supplierRawMaterial = supplierRawMaterialService.getSupplierRawMaterialById(rawMaterialSid);
        if (supplierRawMaterial != null && supplierRawMaterial.getSupplier().getSupplierId().equals(supplier.getSupplierId())) {
            supplierRawMaterialService.deleteSupplierRawMaterial(rawMaterialSid);
        }
        return "redirect:/supplier-raw-materials";
    }

    private Supplier getLoggedInSupplier(HttpSession session) {
        Supplier supplier = (Supplier) session.getAttribute("loggedInSupplier");
        if (supplier == null) {
            User user = (User) session.getAttribute("loggedInUser");
            if (user != null) {
                supplier = supplierService.getSupplierProfile(user.getUserId());
                if (supplier != null) {
                    session.setAttribute("loggedInSupplier", supplier);
                }
            }
        }
        return supplier;
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchProducts(@RequestParam String query) {
        List<SupplierRawMaterial> materials = supplierRawMaterialService.searchProducts(query);

        if (materials.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> productResults = materials.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getRawMaterialSid());
            map.put("name", product.getName());
            map.put("price", product.getUnitCost());
            map.put("description", product.getDescription());
            // map.put("rating", product.getRating());
            map.put("image", product.getImage() != null ? product.getImage() : "/img/default-product.jpg");

            // Include distributor details
            if (product.getSupplier() != null) {
                map.put("supplierId", product.getSupplier().getSupplierId());
                map.put("companyName", product.getSupplier().getCompanyName());
            } else {
                map.put("supplierId", null);
                map.put("companyName", "Unknown Supplier");
            }

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(productResults);
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<SupplierRawMaterial> products = supplierRawMaterialService.getProductsByCategoryId(categoryId);
        if (products.isEmpty()) return ResponseEntity.noContent().build();

        List<Map<String, Object>> productList = products.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }
    
    private Map<String, Object> convertToResponse(SupplierRawMaterial product) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", product.getRawMaterialSid());
        productData.put("name", product.getName());
        productData.put("price", product.getUnitCost());
        productData.put("description", product.getDescription());
        productData.put("stockQuantity", product.getQtyOnHand());
        productData.put("image", product.getImage());
        productData.put("supplierId", product.getSupplier().getSupplierId());
        productData.put("companyName", product.getSupplier().getCompanyName());
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
    
    @GetMapping("/{productId}")
    public ResponseEntity<SupplierRawMaterial> getProductDetails(@PathVariable Long productId) {
        Optional<SupplierRawMaterial> product = supplierRawMaterialService.getProductById(productId);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Return 404 if not found
    }
    
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsBySupplier(@PathVariable Long supplierId) {
        List<SupplierRawMaterial> products = supplierRawMaterialService.getProductsBySupplierId(supplierId);
        if (products.isEmpty()) return ResponseEntity.noContent().build();

        List<Map<String, Object>> productList = products.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<SupplierRawMaterial>> getAllProducts() {
        List<SupplierRawMaterial> products = supplierRawMaterialService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // No products found
        }
        return ResponseEntity.ok(products);
    }
}