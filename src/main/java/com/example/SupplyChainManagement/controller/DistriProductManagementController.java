package com.example.SupplyChainManagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.DistriStockProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import com.example.SupplyChainManagement.service.CategoryService;
import com.example.SupplyChainManagement.service.DistributorService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/distributor-inventory")
public class DistriProductManagementController {

    @Autowired
    private DistributorService distributorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DistributorRepository distributorRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

    @GetMapping
    public String showInventoryPage(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", loggedInUser);

        if (loggedInUser == null) {
            System.out.println("No logged-in user found in session");
            return "redirect:/userRegistration?form=login";
        }

        Optional<Distributor> distributorOpt = distributorRepository.findByUser_UserId(loggedInUser.getUserId());
        if (distributorOpt.isEmpty()) {
            System.out.println("No distributor found for user_id: " + loggedInUser.getUserId());
            return "redirect:/userRegistration?form=login";
        }

        Long distributorId = distributorOpt.get().getDistributorId();
        Distributor distributor = distributorOpt.get();

        // Fetch all inventory items and marketplace items
        List<DistriStockProduct> allInventoryItems = distributorService.getAllInventoryItemsByDistributorId(distributorId);
        List<DistriProduct> marketplaceItems = distributorService.getMarketplaceItemsByDistributorId(distributorId);
        List<Category> categories = categoryService.getAllCategories();

        // Filter out inventory items already in the marketplace
        List<String> marketplaceItemNames = marketplaceItems.stream()
                .map(DistriProduct::getName)
                .collect(Collectors.toList());
        List<DistriStockProduct> availableInventoryItems = allInventoryItems.stream()
                .filter(item -> !marketplaceItemNames.contains(item.getName()))
                .collect(Collectors.toList());

        model.addAttribute("loggedInDistributor", distributor);
        model.addAttribute("categories", categories);
        model.addAttribute("allInventoryItems", availableInventoryItems); // Use filtered list
        model.addAttribute("marketplaceItems", marketplaceItems);
        model.addAttribute("marketplaceItem", new DistriProduct());
        return "distributormanageinventory";
    }

    @PostMapping("/save-marketplace")
    public String saveMarketplaceItem(
            @RequestParam("name") String name,
            @RequestParam("category") Category category,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile imageFile,
            HttpSession session, Model model) throws IOException {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            System.out.println("No logged-in user found in session");
            return "redirect:/userRegistration?form=login";
        }

        Optional<Distributor> distributorOpt = distributorRepository.findByUser_UserId(loggedInUser.getUserId());
        if (distributorOpt.isEmpty()) {
            System.out.println("No distributor found for user_id: " + loggedInUser.getUserId());
            model.addAttribute("error", "No distributor account associated with this user");
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("allInventoryItems", distributorService.getAllInventoryItemsByDistributorId(distributorOpt.get().getDistributorId()));
            return "distributormanageinventory";
        }

        Distributor distributor = distributorOpt.get();

        /* long maxSize = 5 * 1024 * 1024; // 5 MB
        if (!imageFile.isEmpty() && imageFile.getSize() > maxSize) {
            model.addAttribute("error", "Image size exceeds 5MB limit");
            model.addAttribute("loggedInDistributor", distributor);
            model.addAttribute("allInventoryItems", distributorService.getAllInventoryItems());
            model.addAttribute("marketplaceItems", distributorService.getMarketplaceItemsByDistributorId(distributor.getDistributorId()));
            model.addAttribute("categories", categoryService.getAllCategories());
            return "distributormanageinventory";
        } */

        DistriProduct item = new DistriProduct();
        item.setName(name);
        item.setCategory(category);
        item.setPrice(price);
        item.setDescription(description);
        item.setDistributor(distributor);

        String uploadDir = "uploads/";
        if (!imageFile.isEmpty()) {
            try {
                String fileName = distributor.getUser().getUserId() + "_" + imageFile.getOriginalFilename().replaceAll("\\s+", "");
                Path filePath = Paths.get(uploadDir + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, imageFile.getBytes());
                item.setImage(fileName);
                System.out.println("Image saved to: " + filePath.toAbsolutePath());
            } catch (IOException e) {
                model.addAttribute("error", "Image upload failed: " + e.getMessage());
                System.err.println("Image upload failed: " + e.getMessage());
            }
        } else {
            item.setImage(null);
        }
        distributorService.saveMarketplaceItem(item);
        return "redirect:/distributor-inventory";
    }

    @GetMapping("/api/marketplace/{itemId}")
    public ResponseEntity<DistriProduct> getMarketplaceItem(@PathVariable Long itemId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Distributor> distributorOpt = distributorRepository.findByUser_UserId(loggedInUser.getUserId());
        if (distributorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<DistriProduct> itemOpt = distributorService.getMarketplaceItemById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getDistributor().getDistributorId().equals(distributorOpt.get().getDistributorId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(itemOpt.get());
    }

    @PostMapping("/update-marketplace/{itemId}")
    public String updateMarketplaceItem(
            @PathVariable Long itemId,
            @RequestParam("name") String name,
            @RequestParam("category") Category category,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile imageFile,
            HttpSession session, Model model) throws IOException {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            System.out.println("No logged-in user found in session");
            return "redirect:/userRegistration?form=login";
        }

        Optional<Distributor> distributorOpt = distributorRepository.findByUser_UserId(loggedInUser.getUserId());
        if (distributorOpt.isEmpty()) {
            System.out.println("No distributor found for user_id: " + loggedInUser.getUserId());
            model.addAttribute("error", "No distributor account associated with this user");
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("allInventoryItems", distributorService.getAllInventoryItemsByDistributorId(distributorOpt.get().getDistributorId()));
            return "distributormanageinventory";
        }

        Distributor distributor = distributorOpt.get();
        Optional<DistriProduct> itemOpt = distributorService.getMarketplaceItemById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getDistributor().getDistributorId().equals(distributor.getDistributorId())) {
            model.addAttribute("error", "Item not found or you do not have permission to edit it");
            model.addAttribute("loggedInDistributor", distributor);
            model.addAttribute("allInventoryItems", distributorService.getAllInventoryItemsByDistributorId(distributor.getDistributorId()));
            model.addAttribute("marketplaceItems", distributorService.getMarketplaceItemsByDistributorId(distributor.getDistributorId()));
            model.addAttribute("categories", categoryService.getAllCategories());
            return "distributormanageinventory";
        }

        DistriProduct item = itemOpt.get();
        item.setName(name);
        item.setCategory(category);
        item.setPrice(price);
        item.setDescription(description);

        /* long maxSize = 5 * 1024 * 1024; // 5 MB
        if (!imageFile.isEmpty()) {
            if (imageFile.getSize() > maxSize) {
                model.addAttribute("error", "Image size exceeds 5MB limit");
                model.addAttribute("loggedInDistributor", distributor);
                model.addAttribute("allInventoryItems", distributorService.getAllInventoryItems());
                model.addAttribute("marketplaceItems", distributorService.getMarketplaceItemsByDistributorId(distributor.getDistributorId()));
                model.addAttribute("categories", categoryService.getAllCategories());
                return "distributormanageinventory";
            }
            if (item.getImage() != null) {
                Path oldImagePath = Paths.get("src/main/resources/static" + item.getImage());
                Files.deleteIfExists(oldImagePath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, imageFile.getBytes());
            item.setImage("/img/" + fileName);
        } */
        
        String uploadDir = "uploads/";
        if (!imageFile.isEmpty()) {
            try {
                String fileName = distributor.getUser().getUserId() + "_" + imageFile.getOriginalFilename().replaceAll("\\s+", "");
                Path filePath = Paths.get(uploadDir + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, imageFile.getBytes());
                item.setImage(fileName);
                System.out.println("Image saved to: " + filePath.toAbsolutePath());
            } catch (IOException e) {
                model.addAttribute("error", "Image upload failed: " + e.getMessage());
                System.err.println("Image upload failed: " + e.getMessage());
            }
        } else {
            item.setImage(null);
        }

        distributorService.saveMarketplaceItem(item);
        return "redirect:/distributor-inventory";
    }

    @GetMapping("/delete-marketplace/{id}")
    public String deleteMarketplaceItem(@PathVariable("id") Long itemId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            System.out.println("No logged-in user found in session");
            return "redirect:/userRegistration?form=login";
        }

        Optional<Distributor> distributorOpt = distributorRepository.findByUser_UserId(loggedInUser.getUserId());
        if (distributorOpt.isEmpty()) {
            System.out.println("No distributor found for user_id: " + loggedInUser.getUserId());
            return "redirect:/userRegistration?form=login";
        }

        Distributor distributor = distributorOpt.get();
        Optional<DistriProduct> itemOpt = distributorService.getMarketplaceItemById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getDistributor().getDistributorId().equals(distributor.getDistributorId())) {
            model.addAttribute("error", "Item not found or you do not have permission to delete it");
            model.addAttribute("loggedInDistributor", distributor);
            model.addAttribute("allInventoryItems", distributorService.getAllInventoryItemsByDistributorId(distributor.getDistributorId()));
            model.addAttribute("marketplaceItems", distributorService.getMarketplaceItemsByDistributorId(distributor.getDistributorId()));
            model.addAttribute("categories", categoryService.getAllCategories());
            return "distributormanageinventory";
        }

        DistriProduct item = itemOpt.get();
        if (item.getImage() != null) {
            Path imagePath = Paths.get("src/main/resources/static" + item.getImage());
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.out.println("Failed to delete image: " + e.getMessage());
            }
        }

        distributorService.deleteMarketplaceItem(itemId);
        return "redirect:/distributor-inventory";
    }
}