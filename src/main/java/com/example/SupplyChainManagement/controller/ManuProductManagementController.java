package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.ManuStockProduct;
import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.ManuProductRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;
import com.example.SupplyChainManagement.service.CategoryService;
import com.example.SupplyChainManagement.service.SupplierService;
import com.example.SupplyChainManagement.service.UserService;
import com.example.SupplyChainManagement.service.InventoryService;
import com.example.SupplyChainManagement.service.ManuProductService;
import com.example.SupplyChainManagement.service.ManuStockProductService;
import com.example.SupplyChainManagement.service.ManufacturerService;
import com.example.SupplyChainManagement.service.SupplierMaterialService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
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
@RequestMapping("/manuproductmanagement")
public class ManuProductManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ManuProductManagementController.class);
    
    // private final String upload_Dir = "./uploads/";

    @Autowired
    private ManuStockProductService manuStockProductService;
    
    @Autowired
    private ManuProductService manuProductService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ManufacturerService manufacturerService;

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    
    @Autowired
    private ManuProductRepository manuProductRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    
    
    @GetMapping
    public String showManuProducts(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/userRegistration?form=login";
        }
        Long userId = user.getUserId();
        Optional<Manufacturer> manuOpt = manufacturerRepository.findByUser_UserId(userId);
        if (manuOpt.isEmpty()) {
            return "redirect:/manufacturer-dashboard";
        }

        List<ManuStockProduct> manuProducts = manuStockProductService.getProductsByManufacturerId(manuOpt.get().getManufacturerId());
        List<Category> categories = categoryService.getAllCategories();
        // List<InventoryItem> inventoryItems = inventoryService.getAllInventoryItems();
        List<ManuStockProduct> inventoryItems = manuStockProductService.getAllProducts();

        Set<String> existingRawMaterialNames = manuStockProductService.getExistingProductsNamesByManufacturerId(manuOpt.get().getManufacturerId());
        List<ManuStockProduct> availableInventoryItems = inventoryItems.stream()
                .filter(item -> !existingRawMaterialNames.contains(item.getName()))
                .collect(Collectors.toList());

        model.addAttribute("manuProducts", manuProducts);
        model.addAttribute("categories", categories);
        model.addAttribute("inventoryItems", availableInventoryItems);
        model.addAttribute("manuProduct", new ManuProduct());
        model.addAttribute("isEditMode", false);
        model.addAttribute("loggedInManufacturer", manuOpt);
        return "manuAddProduct";
    }
    
    /* @PostMapping("/save")
    public String saveManuProduct(@ModelAttribute ManuProduct manuProduct,
                                         @RequestParam("imageFile") MultipartFile imageFile,
                                         HttpSession session, Model model) {
        Manufacturer manu = getLoggedInManufacturer(session);
        if (manu == null) {
        	return "redirect:/userRegistration?form=login";
        }

        manuProduct.setManufacturer(manu);

        try {
            String uploadDir = "src/main/resources/static/uploads/";
            ManuProduct existingRawMaterial = null;

            if (manuProduct.getProductMid() != null) {
                existingRawMaterial = manuProductService.getManuProductById(manuProduct.getProductMid());
                if (existingRawMaterial == null) {
                    model.addAttribute("error", "Raw material not found.");
                    return "manuAddProduct";
                }

                manuProduct.setName(existingRawMaterial.getName());
                manuProduct.setCategory(existingRawMaterial.getCategory());
                
                
                /* if (existingRawMaterial.getCategory().getCategoryId() != null) {
                    Optional<Category> category = Optional.ofNullable(categoryService.getCategoryBy_CategoryId(existingRawMaterial.getCategory().getCategoryId()));
                    category.ifPresent(ManuProduct::setCategory);
                }
            }  */

            /* if (manuProduct.getDescription() == null || manuProduct.getDescription().trim().isEmpty()) {
                model.addAttribute("error", "Description cannot be empty.");
                return "manuAddProduct";
            }

            if (!imageFile.isEmpty()) {
                try {
                    String fileName = imageFile.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, imageFile.getBytes());
                    manuProduct.setImage(fileName);
                } catch (IOException e) {
                    model.addAttribute("error", "Image upload failed. Please try again.");
                    return "manuAddProduct";
                }
            } else if (existingRawMaterial != null) {
                manuProduct.setImage(existingRawMaterial.getImage());
            }

            manuProductService.saveManuProduct(manuProduct);
            return "redirect:/manu-product-management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save supplier raw material: " + e.getMessage());
            return "manuAddProduct";
        }
    }  */
    // private static final String UPLOAD_DIR = "src/main/resources/static/img/";
    @PostMapping("/save")
    public String saveManuProduct(@ModelAttribute ManuProduct manuProduct,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 HttpSession session, Model model) {
        Manufacturer manu = getLoggedInManufacturer(session);
        if (manu == null) {
            return "redirect:/userRegistration?form=login";
        }

        manuProduct.setManufacturer(manu);

        try {
            String uploadDir = "uploads/";
            ManuProduct existingRawMaterial = null;

            if (manuProduct.getProductMid() != null) {
                existingRawMaterial = manuProductService.getManuProductById(manuProduct.getProductMid());
                if (existingRawMaterial == null) {
                    model.addAttribute("error", "Raw material not found.");
                    return "manuAddProduct";
                }

                manuProduct.setName(existingRawMaterial.getName());
                manuProduct.setCategory(existingRawMaterial.getCategory());
                if (existingRawMaterial.getCategory().getCategoryId() != null) {
                    Optional<Category> category = Optional.ofNullable(categoryService.getCategoryBy_CategoryId(existingRawMaterial.getCategory().getCategoryId()));
                    category.ifPresent(manuProduct::setCategory);
                }
            }

            if (!imageFile.isEmpty()) {
                try {
                    String fileName = manu.getUser().getUserId() + "_" + imageFile.getOriginalFilename().replaceAll("\\s+", "");
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, imageFile.getBytes());
                    manuProduct.setImage(fileName);
                    System.out.println("Image saved to: " + filePath.toAbsolutePath());
                } catch (IOException e) {
                    model.addAttribute("error", "Image upload failed: " + e.getMessage());
                    System.err.println("Image upload failed: " + e.getMessage());
                    return "manuAddProduct";
                }
            } else if (existingRawMaterial != null) {
                manuProduct.setImage(existingRawMaterial.getImage());
            }

            manuProductService.saveManuProduct(manuProduct);
            return "redirect:/manu-product-management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save supplier raw material: " + e.getMessage());
            return "manuAddProduct";
        }
    }
    
    private Manufacturer getLoggedInManufacturer(HttpSession session) {
        Manufacturer manu = (Manufacturer) session.getAttribute("loggedInManufacturer");
        if (manu == null) {
            User user = (User) session.getAttribute("loggedInUser");
            if (user != null) {
                manu = manufacturerService.getManuProfile(user.getUserId());
                if (manu != null) {
                    session.setAttribute("loggedInManufacturer", manu);
                }
            }
        }
        return manu;
    }
    /* @GetMapping("/available-inventory")
    @ResponseBody
    public List<InventoryItem> getAvailableInventoryItems(@RequestParam Long supplierId, Model model) {
        List<InventoryItem> allInventoryItems = inventoryService.getAllInventoryItems();
        Set<String> existingRawMaterialNames = ManuProductService.getExistingRawMaterialNamesBySupplierId(supplierId);
        return allInventoryItems.stream()
                .filter(item -> !existingRawMaterialNames.contains(item.getName()))
                .collect(Collectors.toList());
    } */
   /* @GetMapping("/edit/{rawMaterialSid}")
    public String showEditManuProductForm(@PathVariable Long rawMaterialSid, Model model, HttpSession session) {
        Supplier supplier = getLoggedInSupplier(session);
        if (supplier == null) {
            return "redirect:/login";
        }

        ManuProduct ManuProduct = ManuProductService.getManuProductById(rawMaterialSid);
        if (ManuProduct == null) {
            logger.warn("Raw material not found for rawMaterialSid: {}", rawMaterialSid);
            return "redirect:/supplier-raw-materials";
        }

        if (!ManuProduct.getSupplierRid().equals(supplier.getSupplierId())) {
            logger.warn("Unauthorized access: rawMaterialSid {} does not belong to supplier {}", rawMaterialSid, supplier.getSupplierId());
            return "redirect:/supplier-raw-materials";
        }

        // Ensure the category is loaded or fetched based on categoryRid
        if (ManuProduct.getCategoryRid() != null) {
            Optional<Category> category = categoryService.findCategoryById(ManuProduct.getCategoryRid());
            category.ifPresent(ManuProduct::setCategory); // Set the category if it exists
        }

        logger.debug("ManuProduct for edit with category: {}", ManuProduct);

        List<Category> categories = categoryService.findAllCategories();
        List<InventoryItem> inventoryItems = inventoryService.getAllInventoryItems();

        model.addAttribute("ManuProduct", ManuProduct);
        model.addAttribute("categories", categories);
        model.addAttribute("inventoryItems", inventoryItems);
        model.addAttribute("isEditMode", true);
        model.addAttribute("loggedInSupplier", supplier);
        return "addRawMaterial";
    }*/
    @GetMapping("/delete/{productMid}")
    public String deleteManuProduct(@PathVariable Long productMid, HttpSession session) {
        Manufacturer manu = getLoggedInManufacturer(session);
        if (manu == null) {
        	return "redirect:/userRegistration?form=login";
        }

        ManuProduct manuProduct = manuProductService.getManuProductById(productMid);
        if (manuProduct != null && manuProduct.getManufacturer().getManufacturerId().equals(manu.getManufacturerId())) {
            manuProductService.deleteManuProduct(productMid);
        }
        return "redirect:/manu-product-management";
    }
    
 // Fetch product details for editing
    @GetMapping("/edit/{productMid}")
    @ResponseBody
    public ResponseEntity<ManuProduct> getProductForEdit(@PathVariable Long productMid) {
        Optional<ManuProduct> productOpt = manuProductRepository.findById(productMid);
        if (productOpt.isPresent()) {
            return ResponseEntity.ok(productOpt.get());
        } else {
            return ResponseEntity.notFound().build(); // Returns 404 with no body
        }
    }

    private final String upload_Dir = "src/main/resources/static/uploads/";
    // Update product details
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateProduct(
            @RequestParam("productMid") Long productMid,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") Long categoryId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        Optional<ManuProduct> existingProductOpt = manuProductRepository.findById(productMid);

        if (!existingProductOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        ManuProduct product = existingProductOpt.get();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);

        // Update category
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid category ID"));
        }
        product.setCategory(categoryOpt.get());

        // Handle image update
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(upload_Dir, fileName); // Use Paths.get for safer path construction
                Files.createDirectories(filePath.getParent()); // Ensure directory exists
                imageFile.transferTo(filePath.toFile());
                product.setImage("/uploads/" + fileName);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Failed to upload image: " + e.getMessage()));
            }
        }

        manuProductRepository.save(product);
        return ResponseEntity.ok(Collections.singletonMap("message", "Product updated successfully"));
    }
    
    /* @GetMapping("/manuproduct/{id}")
    @ResponseBody
    public ResponseEntity<ManuProduct> getProductById(@PathVariable("id") Long id) {
        Optional<ManuProduct> product = manuProductRepository.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    @PostMapping("/manuproduct/update")
    public String updateProduct(@RequestParam("productMid") Long productMid,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") BigDecimal price,
                                @RequestParam("categoryId") Long categoryId,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        ManuProduct product = manuProductRepository.findById(productMid).orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        // Handle Image Upload (Optional)
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                product.setImage(imageFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        manuProductRepository.save(product);
        return "redirect:/manuproductmanagement";
    } */


    
    
    /* @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchProducts(@RequestParam String query) {
        List<ManuProduct> materials = ManuProductService.searchProducts(query);

        if (materials.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> productResults = materials.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getRawMaterialSid());
            map.put("name", product.getName());
            map.put("price", product.getUnitPrice());
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
        List<ManuProduct> products = ManuProductService.getProductsByCategoryId(categoryId);
        if (products.isEmpty()) return ResponseEntity.noContent().build();

        List<Map<String, Object>> productList = products.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }
    
    private Map<String, Object> convertToResponse(ManuProduct product) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", product.getRawMaterialSid());
        productData.put("name", product.getName());
        productData.put("price", product.getUnitPrice());
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
    public ResponseEntity<ManuProduct> getProductDetails(@PathVariable Long productId) {
        Optional<ManuProduct> product = ManuProductService.getProductById(productId);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Return 404 if not found
    }
    
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsBySupplier(@PathVariable Long supplierId) {
        List<ManuProduct> products = ManuProductService.getProductsBySupplierId(supplierId);
        if (products.isEmpty()) return ResponseEntity.noContent().build();

        List<Map<String, Object>> productList = products.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<ManuProduct>> getAllProducts() {
        List<ManuProduct> products = ManuProductService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // No products found
        }
        return ResponseEntity.ok(products);
    } */
}