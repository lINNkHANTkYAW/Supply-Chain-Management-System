package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.dto.ItemDTO;
import com.example.SupplyChainManagement.service.CusOrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dto/items")
public class CusOrderItemController {
    private final CusOrderItemService cusOrderItemService;

    public CusOrderItemController(CusOrderItemService cusOrderItemService) {
        this.cusOrderItemService = cusOrderItemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getItemsByCategoryId(@RequestParam Long categoryId) {
        List<ItemDTO> items = cusOrderItemService.getItemsByCategoryId(categoryId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> getItemDetails(@PathVariable Long itemId) {
        ItemDTO item = cusOrderItemService.getItemDetails(itemId);
        return ResponseEntity.ok(item);
    }
}
