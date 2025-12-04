package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ItemType;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.service.InventoryService;
import com.example.SupplyChainManagement.service.ItemTypeService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/suppliermanageinventory")
public class SupplierInventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ItemTypeService itemTypeService;

    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private CategoryRepository categoryRepository; 

    @GetMapping
    public String showInventoryPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
        	return "redirect:/userRegistration?form=login";
        }

        Long userId = user.getUserId();
        Optional<Supplier> supplierOptional = supplierRepository.findByUser_UserId(userId);
        if (supplierOptional.isEmpty()) {
            return "redirect:/supplier-dashboard";
        }

        Supplier supplier = supplierOptional.get();
        Long supplierId = supplier.getSupplierId();

        List<InventoryItem> items = inventoryService.getItemsBySupplierId(supplierId);
        List<ItemType> itemTypes = itemTypeService.getAllItemTypes();
        List<Category> categories = categoryRepository.findAll(); // ✅ Fetch categories

        model.addAttribute("user", user);
        model.addAttribute("supplier", supplier);
        model.addAttribute("inventoryItems", items);
        model.addAttribute("itemTypes", itemTypes);
        model.addAttribute("categories", categories);  // ✅ Add categories to model
        model.addAttribute("supplierId", supplierId);
        model.addAttribute("inventoryItem", new InventoryItem());

        return "suppliermanageinventory";
    }

    @PostMapping
    public String addItem(@Valid @ModelAttribute InventoryItem item, BindingResult result, 
                         HttpSession session, Model model) {
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

        if (result.hasErrors()) {
            List<InventoryItem> items = inventoryService.getItemsBySupplierId(supplierId);
            List<ItemType> itemTypes = itemTypeService.getAllItemTypes();
            model.addAttribute("user", user);
            model.addAttribute("supplier", supplier);
            model.addAttribute("inventoryItems", items);
            model.addAttribute("itemTypes", itemTypes);
            model.addAttribute("supplierId", supplierId);
            return "suppliermanageinventory";
        }

        inventoryService.addItem(supplierId, item);
        return "redirect:/suppliermanageinventory";
    }

    @PostMapping("/update/{id}")
    public String updateItem(@PathVariable Long id, @Valid @ModelAttribute InventoryItem updatedItem, 
                            BindingResult result, HttpSession session, Model model) {
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

        if (result.hasErrors()) {
            List<InventoryItem> items = inventoryService.getItemsBySupplierId(supplierId);
            List<ItemType> itemTypes = itemTypeService.getAllItemTypes();
            model.addAttribute("user", user);
            model.addAttribute("supplier", supplier);
            model.addAttribute("inventoryItems", items);
            model.addAttribute("itemTypes", itemTypes);
            model.addAttribute("supplierId", supplierId);
            model.addAttribute("inventoryItem", updatedItem); // Pre-fill form with updatedItem
            return "suppliermanageinventory";
        }

        inventoryService.updateItem(id, updatedItem);
        return "redirect:/suppliermanageinventory";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
        	return "redirect:/userRegistration?form=login";
        }

        Long userId = user.getUserId();
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        if (supplierOptional.isEmpty()) {
            return "redirect:/supplier-dashboard";
        }

        inventoryService.deleteItem(id);
        return "redirect:/suppliermanageinventory";
    }
}