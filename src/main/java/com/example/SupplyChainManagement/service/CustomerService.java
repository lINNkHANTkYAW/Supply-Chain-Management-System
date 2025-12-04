package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Customer> findByUserId(Long userId) {
        return customerRepository.findByUser_UserId(userId);
    }
    
    public Optional<Customer> findByCustomerId(Long customerId) {
        return customerRepository.findById(customerId);
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
            Customer cus = getCustomerProfile(userId);
            if (cus != null) {
            	cus.getUser().setProfileImg(fileName);;  // Set the file name in the supplier profile
            	customerRepository.save(cus);  // Save the supplier with the updated image
            }

            return fileName;  // Return the file name if upload is successful
        } catch (IOException e) {
            e.printStackTrace();  // Print stack trace for debugging
            return null;  // Return null if there is an error
        }
    }
    
 // ✅ Get Supplier Profile by userId
    public Customer getCustomerProfile(Long userId) {
        Optional<Customer> cusOptional = customerRepository.findByUser_UserId(userId);
        return cusOptional.orElse(null);
    }
}

