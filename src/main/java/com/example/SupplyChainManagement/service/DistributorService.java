package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.DistriStockProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.repository.DistriProductRepository;
import com.example.SupplyChainManagement.repository.DistriStockProductRepository;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class DistributorService {

    private final DistributorRepository distributorRepository;
    private final DistriStockProductRepository stockProductRepository;
    private final DistriProductRepository productRepository;

    public DistributorService(DistributorRepository distributorRepository, DistriStockProductRepository stockProductRepository
    		, DistriProductRepository productRepository) {
        this.distributorRepository = distributorRepository;
        this.stockProductRepository = stockProductRepository;
        this.productRepository = productRepository;
    }

    // Get distributor details by user ID
    public Optional<Distributor> getDistributorByUserId(Long userId) {
        return distributorRepository.findByUser_UserId(userId);  // Adjust the method if necessary
    }

    // Get distributor details by distributor ID
    public Optional<Distributor> getDistributorById(Long distributorId) {
        return distributorRepository.findById(distributorId);
    }
    
 // ✅ Upload Profile Image
    public String uploadProfileImage(Long userId, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return null; // Prevent saving empty files
            }

            // Create a file name based on userId and the original file name
            String fileName = userId + "_" + file.getOriginalFilename().replaceAll("\\s+", ""); // Remove spaces from file name
            String uploadDir = "src/main/resources/static/img/";  // Directory to store the uploaded file

            // Ensure the directory exists
            Files.createDirectories(Paths.get(uploadDir));

            // Save the file to the specified directory
            Files.copy(file.getInputStream(), Paths.get(uploadDir + fileName), StandardCopyOption.REPLACE_EXISTING);

            // Update the supplier's profile image in the database
            Distributor distributor = getDistributorProfile(userId);
            if (distributor != null) {
            	distributor.getUser().setProfileImg(fileName);;  // Set the file name in the supplier profile
            	distributorRepository.save(distributor);  // Save the supplier with the updated image
            }

            return fileName;  // Return the file name if upload is successful
        } catch (IOException e) {
            e.printStackTrace();  // Print stack trace for debugging
            return null;  // Return null if there is an error
        }
    }
    
 // ✅ Get Supplier Profile by userId
    public Distributor getDistributorProfile(Long userId) {
        Optional<Distributor> distriOptional = distributorRepository.findByUser_UserId(userId);
        return distriOptional.orElse(null);
    }
    
    public List<DistriStockProduct> getAllInventoryItemsByDistributorId(Long distributorId) {
        return stockProductRepository.findByDistributorId(distributorId);
    }
    
    public List<DistriProduct> getMarketplaceItemsByDistributorId(Long distributorId) {
        return productRepository.findByDistributor_DistributorId(distributorId);
    }
    
    public List<DistriStockProduct> getStockItemsByDistributorId(Long distributorId) {
        return stockProductRepository.findByDistributorId(distributorId);
    }
    
    public Optional<DistriProduct> getMarketplaceItemById(Long id) {
        return productRepository.findById(id);
    }
    
    public void saveMarketplaceItem(DistriProduct item) {
        productRepository.save(item);
    }
    
    public void deleteMarketplaceItem(Long itemId) {
        productRepository.deleteById(itemId); // Adjust based on your repo
    }
}


