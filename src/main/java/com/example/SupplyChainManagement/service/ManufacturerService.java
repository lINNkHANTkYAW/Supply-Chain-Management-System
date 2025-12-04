package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    public ManufacturerService(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }
    
    public Optional<Manufacturer> findByUserId(Long userId) {
        return manufacturerRepository.findByUser_UserId(userId);
    }

    // Get distributor details by user ID
    public Optional<Manufacturer> getManufacturerByUserId(Long userId) {
        return manufacturerRepository.findByUser_UserId(userId);  // Adjust the method if necessary
    }

    // Get distributor details by distributor ID
    public Optional<Manufacturer> getManufacturerById(Long manufacturerId) {
        return manufacturerRepository.findById(manufacturerId);
    }
    
    
 // ✅ Get Supplier Profile by userId
    public Manufacturer getManuProfile(Long userId) {
        Optional<Manufacturer> manuOptional = manufacturerRepository.findByUser_UserId(userId);
        return manuOptional.orElse(null);
    }

    // ✅ Update Supplier Profile by userId
    public Manufacturer updateProfile(Long userId, Manufacturer updatedManufacturer) {
        Optional<Manufacturer> manuOptional = manufacturerRepository.findByUser_UserId(userId);
        if (manuOptional.isEmpty()) return null;

        Manufacturer manufacturer = manuOptional.get();
        manufacturer.setCompanyName(updatedManufacturer.getCompanyName());
        manufacturer.setAddress(updatedManufacturer.getAddress());
        manufacturer.setContactInfo(updatedManufacturer.getContactInfo());
        manufacturer.setBio(updatedManufacturer.getBio());

        return manufacturerRepository.save(manufacturer);
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
            Manufacturer manufacturer = getManuProfile(userId);
            if (manufacturer != null) {
            	manufacturer.getUser().setProfileImg(fileName);;  // Set the file name in the supplier profile
            	manufacturerRepository.save(manufacturer);  // Save the supplier with the updated image
            }

            return fileName;  // Return the file name if upload is successful
        } catch (IOException e) {
            e.printStackTrace();  // Print stack trace for debugging
            return null;  // Return null if there is an error
        }
        
       
    }
}
