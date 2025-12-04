package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Method to fetch all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Method to get category by ID
    public Category getCategoryBy_CategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    // Method to add a new category
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Method to update an existing category
    public Category updateCategory(Long categoryId, Category category) {
        if (categoryRepository.existsById(categoryId)) {
            category.setCategoryId(categoryId);
            return categoryRepository.save(category);
        }
        return null;
    }

    // Method to delete a category
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
