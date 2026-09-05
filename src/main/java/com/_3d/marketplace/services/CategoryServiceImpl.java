package com._3d.marketplace.services;

import com._3d.marketplace.entity.Category;

import com._3d.marketplace.exceptions.CategoryDuplicateException;
import com._3d.marketplace.exceptions.CategoryInUseException;
import com._3d.marketplace.exceptions.CategoryNotFoundException;
import com._3d.marketplace.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{


    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> getCategories(PageRequest pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Category createCategory(String description) throws CategoryDuplicateException {
        List<Category> categories = categoryRepository.findByDescription(description);
        if(categories.isEmpty())
            return categoryRepository.save(new Category(description));
        throw new CategoryDuplicateException();
    }

    @Transactional
    public Category updateCategory(Long categoryId, String description) throws CategoryDuplicateException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No se encontró la categoría con el id: " + categoryId));

        boolean taken = categoryRepository.findByDescription(description).stream()
                .anyMatch(other -> !other.getId().equals(categoryId));
        if (taken)
            throw new CategoryDuplicateException();

        category.setDescription(description);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No se encontró la categoría con el id: " + categoryId));

        if (category.getProducts() != null && !category.getProducts().isEmpty())
            throw new CategoryInUseException("No se puede eliminar una categoría que tiene productos asociados.");

        categoryRepository.delete(category);
    }
}
