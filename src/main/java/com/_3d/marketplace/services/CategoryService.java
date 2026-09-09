package com._3d.marketplace.services;

import com._3d.marketplace.entity.dto.CategoryResponse;
import com._3d.marketplace.exceptions.CategoryDuplicateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {

    public Page<CategoryResponse> getCategories(Pageable pageable);

    public Optional<CategoryResponse> getCategoryById(Long categoryId);

    public CategoryResponse createCategory(String description) throws CategoryDuplicateException;

    public CategoryResponse updateCategory(Long categoryId, String description) throws CategoryDuplicateException;

    public void deleteCategory(Long categoryId);
}
