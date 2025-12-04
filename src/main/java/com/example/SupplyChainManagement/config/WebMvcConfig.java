package com.example.SupplyChainManagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve /uploads/** from the uploads/ directory at the project root
         registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/"); 

        // Serve /invoices/** from the invoices/ directory at the project root
        registry.addResourceHandler("/invoices/**")
                .addResourceLocations("file:./invoices/");
    }
}