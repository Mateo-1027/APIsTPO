package com._3d.marketplace.repositories;

import com._3d.marketplace.entity.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryRepository {

    public ArrayList<Category> categories = new ArrayList<>(
            Arrays.asList(
                    Category.builder().id(1).name("Electrónica y 3D").build(),
                    Category.builder().id(2).name("Hogar y Decoración").build(),
                    Category.builder().id(3).name("Indumentaria").build()
            )
    );

    public List<Category> getAll() {
        return categories;
    }

    public Category getById(int id) {
        return categories.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Category save(Category category) {
        categories.add(category);
        return category;
    }
}
