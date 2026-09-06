package com._3d.marketplace.entity.dto;

import com._3d.marketplace.entity.Category;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String description;

    public static CategoryResponse from(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setDescription(category.getDescription());
        return response;
    }
}
