package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category findByCategoryId(Long categoryId);
}
