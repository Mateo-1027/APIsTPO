package com._3d.marketplace.repositories;

import com._3d.marketplace.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    Page<Product> findByCategoryIdAndPriceBetween(Long categoryId, Double minPrice, Double maxPrice, Pageable pageable);
}
