package com._3d.marketplace.services;

import com._3d.marketplace.entity.Category;
import com._3d.marketplace.repositories.CategoryRepository;

import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Category> getCategories() {
        return categoryRepository.getAll();
    }

    public Category getCategoryById(int categoryId) {
        return categoryRepository.getById(categoryId);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
}
