package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.Manufacturer;
//import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    // Constructor injection for repositories
    public SupplierService(SupplierRepository supplierRepository, UserRepository userRepository) {
        this.supplierRepository = supplierRepository;
    }

    // ✅ Get Supplier Profile by userId
    public Supplier getSupplierProfile(Long userId) {
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        return supplierOptional.orElse(null);
    }

    // ✅ Update Supplier Profile by userId
    public Supplier updateProfile(Long userId, Supplier updatedSupplier) {
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        if (supplierOptional.isEmpty()) return null;

        Supplier supplier = supplierOptional.get();
        supplier.setCompanyName(updatedSupplier.getCompanyName());
        supplier.setAddress(updatedSupplier.getAddress());
        supplier.setContactInfo(updatedSupplier.getContactInfo());
        supplier.setBio(updatedSupplier.getBio());

        return supplierRepository.save(supplier);
    }

    // ✅ Upload Profile Image
    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Create a file name based on userId and the original file name
        String fileName = userId + "_" + file.getOriginalFilename().replaceAll("\\s+", ""); // Remove spaces from file name
        String uploadDir = "src/main/resources/static/img/";  // Directory to store the uploaded file

        // Ensure the directory exists
        Files.createDirectories(Paths.get(uploadDir));

        // Save the file to the specified directory
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update the supplier's profile image in the database
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        if (supplierOptional.isPresent()) {
            Supplier supplier = supplierOptional.get();
            supplier.getUser().setProfileImg(fileName);
            supplierRepository.save(supplier);
        } else {
            throw new IllegalArgumentException("Supplier not found for user ID: " + userId);
        }

        return fileName;  // Return the file name if upload is successful
    }
 // Get distributor details by distributor ID
    public Optional<Supplier> getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId);
    }
    
    public Optional<Supplier> findByUserId(Long userId) {
        return supplierRepository.findByUser_UserId(userId);
    }
}