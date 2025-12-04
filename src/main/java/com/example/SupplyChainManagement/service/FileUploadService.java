package com.example.SupplyChainManagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {
    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

    public String saveFile(MultipartFile file) {
        try {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            file.transferTo(filePath);
            return "/img/" + filename; // Return relative path
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}

