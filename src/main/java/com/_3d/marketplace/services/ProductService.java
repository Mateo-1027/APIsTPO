package com._3d.marketplace.services;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.ProductRequest;
import com._3d.marketplace.entity.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    Page<ProductResponse> searchProducts(String name, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable);
    Page<ProductResponse> getProductsBySeller(Long sellerId, Pageable pageable);
    Optional<ProductResponse> getProductById(Long id);
    ProductResponse createProduct(ProductRequest request, User seller);
    ProductResponse updateProduct(Long id, ProductRequest request, User user);
    void deleteProduct(Long id, User user);
    ProductResponse updateStock(Long id, Integer quantity, User user);
    ProductResponse applyDiscount(Long id, Double discount, User user);
    ProductResponse addImageToProduct(Long productId, org.springframework.web.multipart.MultipartFile file, User user) throws java.io.IOException;
    ProductResponse deleteProductImage(Long productId, Long imageId, User user);
}
