package com._3d.marketplace.services;

import com._3d.marketplace.entity.dto.ProductRequest;
import com._3d.marketplace.entity.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductResponse> getProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    ProductResponse updateStock(Long id, Integer quantity);
    ProductResponse applyDiscount(Long id, Double discount);
}
