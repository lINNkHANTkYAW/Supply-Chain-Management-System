package com.example.SupplyChainManagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.SupplyChainManagement.dto.ManuProductDTO;
import com.example.SupplyChainManagement.dto.ManuProductUpdateDTO;
import com.example.SupplyChainManagement.dto.NewManuProductDTO;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Products;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.ManuProductRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;
import com.example.SupplyChainManagement.repository.SupplierMaterialRepository;

import jakarta.transaction.Transactional;

@Service
public class ManuProductService {
	
	private final ManuProductRepository manuProductRepository;
	private final ManufacturerRepository manufacturerRepository;
	private final CategoryRepository categoryRepository;
	
	public ManuProductService (ManuProductRepository manuProductRepository, CategoryRepository categoryRepository, ManufacturerRepository manufacturerRepository) {
		this.manuProductRepository = manuProductRepository;
		this.categoryRepository = categoryRepository;
		this.manufacturerRepository = manufacturerRepository;
	}

	public List<ManuProduct> getProductsBySeller(Long sellerId) {
	    return manuProductRepository.findByUser_UserId(sellerId);
	}
	
	public List<ManuProduct> getAllProducts() {
        return manuProductRepository.findAll();
    }
	
	public List<ManuProduct> getProductsByCategoryId(Long categoryId) {
        return manuProductRepository.findByCategory_CategoryId(categoryId);
    }
	
	public Optional<ManuProduct> getProductById(Long productId) {
        return manuProductRepository.findById(productId);
    }
	
	public ManuProduct getManuProductById(Long productMid) {
        Optional<ManuProduct> optional = manuProductRepository.findById(productMid);
        return optional.orElse(null);
    }
	
	public List<ManuProduct> getProductsByManufacturerId(Long manufacturerId) {
        return manuProductRepository.findByManufacturer_ManufacturerId(manufacturerId);
    }
	
	/* public List<ManuProductDTO> getAllProducts() {
        return manuProductRepository.findAll()
                .stream()
                .map(ManuProductDTO::new)
                .collect(Collectors.toList());
    } */
	
	public List<ManuProduct> searchProducts(String query) {
        return manuProductRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
	
	public List<NewManuProductDTO> getAllInventoryItems(Long manufacturerId) {
        return manuProductRepository.findByManufacturer_ManufacturerId(manufacturerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Add a new inventory item
    public NewManuProductDTO addInventoryItem(NewManuProductDTO itemDTO, Long manufacturerId) {
        ManuProduct item = new ManuProduct();
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
        ManuProduct savedItem = manuProductRepository.save(item);
        return mapToDTO(savedItem);
    }

    // Update an inventory item
    public NewManuProductDTO updateInventoryItem(Long itemId, NewManuProductDTO itemDTO) {
        ManuProduct item = manuProductRepository.findById(itemId)
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

        ManuProduct updatedItem = manuProductRepository.save(item);
        return mapToDTO(updatedItem);
    }

    // Delete an inventory item
    public void deleteInventoryItem(Long itemId) {
        manuProductRepository.deleteById(itemId);
    }

    // Map entity to DTO
    private NewManuProductDTO mapToDTO(ManuProduct item) {
        NewManuProductDTO dto = new NewManuProductDTO();
        dto.setProductMid(item.getProductMid());
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
        ManuProduct item = manuProductRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return mapToDTO(item);
    }
    
    public Set<String> getExistingProductsNamesByManufacturerId(Long manufacturerId) {
        return manuProductRepository.findByManufacturer_ManufacturerId(manufacturerId)  // Updated method name
                .stream()
                .map(ManuProduct::getName)
                .filter(name -> name != null)
                .collect(Collectors.toSet());
    }
    
    public ManuProduct saveManuProduct(ManuProduct manuProduct) {
        return manuProductRepository.save(manuProduct);
    }
    public void deleteManuProduct(Long productMid) {
        manuProductRepository.deleteById(productMid);
    }
    
    @Transactional
    public ManuProduct updateManuProduct(Long id, ManuProductUpdateDTO updateDTO) {
        ManuProduct product = manuProductRepository.findById(id)
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

        return manuProductRepository.save(product);
    }
    
    

}
