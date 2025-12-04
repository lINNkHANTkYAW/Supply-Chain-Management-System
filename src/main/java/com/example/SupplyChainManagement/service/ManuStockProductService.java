package com.example.SupplyChainManagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


import com.example.SupplyChainManagement.dto.ManuProductUpdateDTO;
import com.example.SupplyChainManagement.dto.NewManuProductDTO;

import com.example.SupplyChainManagement.model.ManuStockProduct;
import com.example.SupplyChainManagement.model.Manufacturer;


import com.example.SupplyChainManagement.repository.CategoryRepository;

import com.example.SupplyChainManagement.repository.ManuStockProductRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;


import jakarta.transaction.Transactional;

@Service
public class ManuStockProductService {
	
	private final ManuStockProductRepository manuStockProductRepository;
	private final ManufacturerRepository manufacturerRepository;
	private final CategoryRepository categoryRepository;
	
	public ManuStockProductService (ManuStockProductRepository manuStockProductRepository, CategoryRepository categoryRepository, ManufacturerRepository manufacturerRepository) {
		this.manuStockProductRepository = manuStockProductRepository;
		this.categoryRepository = categoryRepository;
		this.manufacturerRepository = manufacturerRepository;
	}

	public List<ManuStockProduct> getProductsBySeller(Long sellerId) {
	    return manuStockProductRepository.findByUser_UserId(sellerId);
	}
	
	public List<ManuStockProduct> getAllProducts() {
        return manuStockProductRepository.findAll();
    }
	
	public List<ManuStockProduct> getProductsByCategoryId(Long categoryId) {
        return manuStockProductRepository.findByCategory_CategoryId(categoryId);
    }
	
	public Optional<ManuStockProduct> getProductById(Long productId) {
        return manuStockProductRepository.findById(productId);
    }
	
	public ManuStockProduct getManuStockProductById(Long productMid) {
        Optional<ManuStockProduct> optional = manuStockProductRepository.findById(productMid);
        return optional.orElse(null);
    }
	
	public List<ManuStockProduct> getProductsByManufacturerId(Long manufacturerId) {
        return manuStockProductRepository.findByManufacturer_ManufacturerId(manufacturerId);
    }
	
	/* public List<ManuProductDTO> getAllProducts() {
        return manuProductRepository.findAll()
                .stream()
                .map(ManuProductDTO::new)
                .collect(Collectors.toList());
    } */
	
	public List<ManuStockProduct> searchProducts(String query) {
        return manuStockProductRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
	
	public List<NewManuProductDTO> getAllInventoryItems(Long manufacturerId) {
        return manuStockProductRepository.findByManufacturer_ManufacturerId(manufacturerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    } 

    // Add a new inventory item
    public NewManuProductDTO addInventoryItem(NewManuProductDTO itemDTO, Long manufacturerId) {
        ManuStockProduct item = new ManuStockProduct();
        item.setName(itemDTO.getName());
        // item.setDescription(itemDTO.getDescription());
        // item.setPrice(itemDTO.getPrice());
        item.setCost(itemDTO.getCost());
        item.setStockQuantity(itemDTO.getStockQuantity());
        item.setCategory(categoryRepository.findById(itemDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));
        item.setImage(itemDTO.getImage());
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        item.setManufacturer(manufacturer);
        item.setAddedDate(itemDTO.getAddedDate());
        ManuStockProduct savedItem = manuStockProductRepository.save(item);
        return mapToDTO(savedItem);
    }

    // Update an inventory item
    public NewManuProductDTO updateInventoryItem(Long itemId, NewManuProductDTO itemDTO) {
        ManuStockProduct item = manuStockProductRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(itemDTO.getName());
        // item.setDescription(itemDTO.getDescription());
        // item.setPrice(itemDTO.getPrice());
        item.setCost(itemDTO.getCost());
        item.setStockQuantity(itemDTO.getStockQuantity());
        
        if (itemDTO.getCategoryId() != null) {
            item.setCategory(categoryRepository.findById(itemDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found")));
        }
        /* item.setCategory(categoryRepository.findById(itemDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"))); */
        // item.setImage(itemDTO.getImage());

        ManuStockProduct updatedItem = manuStockProductRepository.save(item);
        return mapToDTO(updatedItem);
    }

    // Delete an inventory item
    public void deleteInventoryItem(Long itemId) {
    	manuStockProductRepository.deleteById(itemId);
    }

    // Map entity to DTO
    private NewManuProductDTO mapToDTO(ManuStockProduct item) {
        NewManuProductDTO dto = new NewManuProductDTO();
        dto.setProductMid(item.getStockMid());
        dto.setName(item.getName());
        // dto.setDescription(item.getDescription());
        // dto.setPrice(item.getPrice());
        dto.setCost(item.getCost());
        dto.setStockQuantity(item.getStockQuantity());
        dto.setCategoryName(item.getCategory().getCategoryName());
        dto.setCostPerUnit(item.getCost().divide(BigDecimal.valueOf(item.getStockQuantity()), 2, BigDecimal.ROUND_HALF_UP)); // Calculate cost per unit
        dto.setImage(item.getImage());
        dto.setAddedDate(item.getAddedDate());
        return dto;
    }
    
    public NewManuProductDTO getInventoryItem(Long itemId) {
        ManuStockProduct item = manuStockProductRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return mapToDTO(item);
    }
    
    public Set<String> getExistingProductsNamesByManufacturerId(Long manufacturerId) {
        return manuStockProductRepository.findByManufacturer_ManufacturerId(manufacturerId)  // Updated method name
                .stream()
                .map(ManuStockProduct::getName)
                .filter(name -> name != null)
                .collect(Collectors.toSet());
    }
    
    public ManuStockProduct saveManuStockProduct(ManuStockProduct manuProduct) {
        return manuStockProductRepository.save(manuProduct);
    }
    public void deleteManuStockProduct(Long productMid) {
    	manuStockProductRepository.deleteById(productMid);
    }
    
    @Transactional
    public ManuStockProduct updateManuStockProduct(Long id, ManuProductUpdateDTO updateDTO) {
    	ManuStockProduct product = manuStockProductRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update fields from DTO
        if (updateDTO.getName() != null) {
            product.setName(updateDTO.getName());
        }
        if (updateDTO.getStockQuantity() != null) {
            product.setStockQuantity(updateDTO.getStockQuantity());
        }
        if (updateDTO.getCostPerUnit() != null) {
            product.setCost(updateDTO.getCostPerUnit());
        }
        if (updateDTO.getCost() != null) {
            product.setTotalCost(updateDTO.getCost());
        }
        
        // Update addedDate to current date
        product.setAddedDate(LocalDate.now());

        return manuStockProductRepository.save(product);
    }
}
