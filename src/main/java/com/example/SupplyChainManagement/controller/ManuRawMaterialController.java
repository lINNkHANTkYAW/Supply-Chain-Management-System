package com.example.SupplyChainManagement.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.SupplyChainManagement.dto.ManuRawMaterialDTO;
import com.example.SupplyChainManagement.model.ManuRawMaterial;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.service.ManuRawMaterialService;
import com.example.SupplyChainManagement.service.ManufacturerService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/manu-raw-materials")
public class ManuRawMaterialController {

    @Autowired
    private ManuRawMaterialService manuRawMaterialService;
    
    @Autowired
    private ManufacturerService manufacturerService;

    /* @GetMapping
    public ResponseEntity<List<ManuRawMaterial>> getAllDeliveredItems() {
        List<ManuRawMaterial> deliveredItems = manuRawMaterialService.getAllDeliveredItems();
        return ResponseEntity.ok(deliveredItems);
    } */
    
    /* @GetMapping
    public List<Map<String, Object>> getOrders(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        Optional<Manufacturer> manu = manufacturerService.findByUserId(user.getUserId());
        List<ManuRawMaterial> orders = manuRawMaterialService.getMaterialsForManufacturer(manu.get().getManufacturerId());
        return formatOrdersResponse(orders);
    } */
    
    @GetMapping
    public ResponseEntity<List<ManuRawMaterialDTO>> getAllRawMaterials() {
        List<ManuRawMaterialDTO> rawMaterials = manuRawMaterialService.getAllRawMaterials();
        return ResponseEntity.ok(rawMaterials);
    }
    
    private List<Map<String, Object>> formatOrdersResponse(List<ManuRawMaterial> orders) {
        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getRawMaterialMid());
            orderMap.put("manufacturerId", order.getManufacturer().getManufacturerId());
            orderMap.put("manufacturerUserId", order.getManufacturer().getUser().getUserId());
            orderMap.put("companyName", order.getManufacturer().getUser().getUsername());
            orderMap.put("name", order.getName());
            orderMap.put("qtyOnHand", order.getQtyOnHand());
            orderMap.put("unitPrice", order.getUnitPrice());
            orderMap.put("categoryName", order.getCategory().getCategoryName());
            orderMap.put("unitCost", order.getUnitCost());
            orderMap.put("image", order.getImage());
            orderMap.put("addedDate", order.getAddedDate());
            return orderMap;
        }).collect(Collectors.toList());
    }
    
    @PostMapping("/update-quantity")
    public ResponseEntity<String> updateQuantity(@RequestParam Long itemId, @RequestParam int newQuantity) {
        String message = manuRawMaterialService.updateQuantity(itemId, newQuantity);
        return ResponseEntity.ok(message);
    }
    
    /* @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteDeliveredItem(@PathVariable Long itemId) {
        manuRawMaterialService.deleteDeliveredItem(itemId);
        return ResponseEntity.noContent().build();
    } */
    
    /* @GetMapping
    public ResponseEntity<List<ManuRawMaterial>> getDeliveredItemsByManufacturer(@RequestParam Long manufacturerId) {
        List<ManuRawMaterial> deliveredItems = manuRawMaterialService.getDeliveredItemsByManufacturer(manufacturerId);
        return ResponseEntity.ok(deliveredItems);
    } */
    
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteDeliveredItem(
            @PathVariable Long itemId,
            @RequestParam Long manufacturerId) {
        String message = manuRawMaterialService.deleteDeliveredItem(itemId, manufacturerId);
        return ResponseEntity.ok(message);
    }
}