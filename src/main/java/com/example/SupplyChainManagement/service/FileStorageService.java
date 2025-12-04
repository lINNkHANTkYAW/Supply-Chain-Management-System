package com.example.SupplyChainManagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

	// private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    
	/* private static final String uploadDir = "src/main/resources/static/img/";
    
    

    public String saveImageFile(MultipartFile file) {
        // Get the original file extension
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // Generate a more readable filename based on your needs, e.g., using product name or timestamp
        String newFilename = "product_" + System.currentTimeMillis() + extension;

        // Define the directory path where the images will be stored
        Path targetLocation = Paths.get(uploadDir).resolve(newFilename);

        try {
            // Save the file to the specified location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Please try again!", e);
        }

        return "/img/" + newFilename; // Return the relative path for storing in the DB
    } */
	
	private final Path uploadDir = Paths.get("src/main/resources/static/uploads");

    public String saveImageFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Generate a unique filename
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Ensure the upload directory exists
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Save the file to the upload directory
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Return the relative URL for the image
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
    
    /* public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Resource resource = (Resource) new UrlResource(filePath.toUri());
            if (((AbstractFileResolvingResource) resource).exists() || ((AbstractFileResolvingResource) resource).isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not load file: " + filename, e);
        }
    } */

}
