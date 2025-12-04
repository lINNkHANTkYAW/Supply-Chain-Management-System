package com.example.SupplyChainManagement.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.repository.SupplierMaterialRepository;

@Service
public class SupplierMaterialService {

	private final SupplierMaterialRepository supplierMaterialRepository;
	
	public SupplierMaterialService (SupplierMaterialRepository supplierMaterialRepository) {
		this.supplierMaterialRepository = supplierMaterialRepository;
	}
	public List<SupplierRawMaterial> getProductsBySeller(Long sellerId) {
	    return supplierMaterialRepository.findByUserUserId(sellerId);
	}
	
	public SupplierRawMaterial saveSupplierRawMaterial(SupplierRawMaterial supplierRawMaterial) {
        return supplierMaterialRepository.save(supplierRawMaterial);
    }

    public SupplierRawMaterial getSupplierRawMaterialById(Long rawMaterialSid) {
        Optional<SupplierRawMaterial> optional = supplierMaterialRepository.findById(rawMaterialSid);
        return optional.orElse(null);
    }

    public List<SupplierRawMaterial> getSupplierRawMaterialsBySupplierId(Long supplierId) {
        return supplierMaterialRepository.findBySupplierSupplierId(supplierId);  // Updated method name
    }

    public void deleteSupplierRawMaterial(Long rawMaterialSid) {
        supplierMaterialRepository.deleteById(rawMaterialSid);
    }
    
 // New method to get a set of raw material names already added by a supplier
    public Set<String> getExistingRawMaterialNamesBySupplierId(Long supplierId) {
        return supplierMaterialRepository.findBySupplierSupplierId(supplierId)  // Updated method name
                .stream()
                .map(SupplierRawMaterial::getName)
                .filter(name -> name != null)
                .collect(Collectors.toSet());
    }
    
    public List<SupplierRawMaterial> searchProducts(String query) {
        return supplierMaterialRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
    
    public List<SupplierRawMaterial> getProductsByCategoryId(Long categoryId) {
        return supplierMaterialRepository.findByCategory_CategoryId(categoryId);
    }
    
    public Optional<SupplierRawMaterial> getProductById(Long productId) {
        return supplierMaterialRepository.findById(productId);
    }
    
    public List<SupplierRawMaterial> getProductsBySupplierId(Long supplierId) {
        return supplierMaterialRepository.findBySupplier_SupplierId(supplierId);
    }
    
    public List<SupplierRawMaterial> getAllProducts() {
        return supplierMaterialRepository.findAll();
    }
}


