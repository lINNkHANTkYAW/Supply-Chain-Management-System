/* package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.*;
import com.example.SupplyChainManagement.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manufacturermanageinventory")
public class ManufacturerInventoryController {

    @Autowired
    private ManuOrderService manuOrderService;

    @Autowired
    private ManuRawMaterialService manuRawMaterialService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ManufacturerService manufacturerService; */

    /* @GetMapping
    public String showInventoryPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Manufacturer manufacturer = manufacturerService.findByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        model.addAttribute("manufacturerId", manufacturer.getManufacturerId());
        return "manufacturerInventory";
    } */

    /* @GetMapping("/api/manu-orders/completed")
    @ResponseBody
    public ResponseEntity<List<ManuOrder>> getCompletedOrders(@RequestParam Long manufacturerId) {
        List<ManuOrder> orders = manuOrderService.getCompletedOrders(manufacturerId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/api/manu-orders/{orderId}")
    @ResponseBody
    public ResponseEntity<ManuOrder> updateOrder(@PathVariable Long orderId, @RequestBody ManuOrder updatedOrder) {
        ManuOrder order = manuOrderService.updateOrder(orderId, updatedOrder);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/api/manu-orders/{orderId}")
    @ResponseBody
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        manuOrderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/manu-raw-materials")
    @ResponseBody
    public ResponseEntity<List<ManuRawMaterial>> getRawMaterials(@RequestParam Long manufacturerId) {
        List<ManuRawMaterial> rawMaterials = manuRawMaterialService.getRawMaterials(manufacturerId);
        return ResponseEntity.ok(rawMaterials);
    }

    @PostMapping("/api/manu-raw-materials")
    @ResponseBody
    public ResponseEntity<ManuRawMaterial> addRawMaterial(@RequestBody ManuRawMaterial rawMaterial) {
        ManuRawMaterial savedRawMaterial = manuRawMaterialService.addRawMaterial(rawMaterial);
        return ResponseEntity.ok(savedRawMaterial);
    }

    @PutMapping("/api/manu-raw-materials/{id}")
    @ResponseBody
    public ResponseEntity<ManuRawMaterial> updateRawMaterial(@PathVariable Long id, @RequestBody ManuRawMaterial updatedRawMaterial) {
        ManuRawMaterial rawMaterial = manuRawMaterialService.updateRawMaterial(id, updatedRawMaterial);
        return ResponseEntity.ok(rawMaterial);
    }

    @DeleteMapping("/api/manu-raw-materials/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable Long id) {
        manuRawMaterialService.deleteRawMaterial(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
} */

package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ItemType;
import com.example.SupplyChainManagement.model.ManuInventoryItem;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.ItemTypeRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.service.CategoryService;
import com.example.SupplyChainManagement.service.InventoryService;
import com.example.SupplyChainManagement.service.ItemTypeService;
import com.example.SupplyChainManagement.service.ManuInventoryService;
import com.example.SupplyChainManagement.service.ManuProductService;
import com.example.SupplyChainManagement.service.ManufacturerService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/manu/inventory")
public class ManufacturerInventoryController {
	
	@Autowired
    private ManuInventoryService inventoryService;
	
	@Autowired
    private ManuProductService manuProductService;
	
	@Autowired
    private ManufacturerService manufacturerService;
	
	@Autowired
    private CategoryService categoryService;
	
	@GetMapping("/name/{name}")
    public ResponseEntity<ManuInventoryItem> getInventoryItemByName(@PathVariable String name) {
        ManuInventoryItem item = inventoryService.findByName(name);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }
	
	@GetMapping("/available-inventory")
    @ResponseBody
    public List<ManuInventoryItem> getAvailableInventoryItems(@RequestParam Long manufacturerId, Model model) {
        List<ManuInventoryItem> allInventoryItems = inventoryService.getManufacturerInventory();
        Set<String> existingProductNames = manuProductService.getExistingProductsNamesByManufacturerId(manufacturerId);
        return allInventoryItems.stream()
                .filter(item -> !existingProductNames.contains(item.getName()))
                .collect(Collectors.toList());
    }
	
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
            String uploadDir = "uploads/"; // Save to project root uploads/
            ManuProduct existingRawMaterial = null;

            if (manuProduct.getProductMid() != null) {
                existingRawMaterial = manuProductService.getManuProductById(manuProduct.getProductMid());
                if (existingRawMaterial == null) {
                    model.addAttribute("error", "Manu product not found.");
                    return "manuAddProduct";
                }

                manuProduct.setName(existingRawMaterial.getName());
                manuProduct.setCategory(existingRawMaterial.getCategory());
                
                if (existingRawMaterial.getCategory().getCategoryId() != null) {
                    Optional<Category> category = Optional.ofNullable(categoryService.getCategoryBy_CategoryId(existingRawMaterial.getCategory().getCategoryId()));
                    category.ifPresent(manuProduct::setCategory);
                }
            }

            if (manuProduct.getDescription() == null || manuProduct.getDescription().trim().isEmpty()) {
                model.addAttribute("error", "Description cannot be empty.");
                return "manuAddProduct";
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
            System.out.println("Manu product saved: " + manuProduct.getName() + ", image: " + manuProduct.getImage());
            return "redirect:/manu-product-management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save supplier raw material: " + e.getMessage());
            System.err.println("Failed to save raw material: " + e.getMessage());
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
}