package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.PriceEstimateRequest;
import com._3d.marketplace.entity.dto.PriceEstimateResponse;
import com._3d.marketplace.entity.dto.ProductRequest;
import com._3d.marketplace.entity.dto.ProductResponse;
import com._3d.marketplace.services.PricingService;
import com._3d.marketplace.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PricingService pricingService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductResponse> products;
        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId, PageRequest.of(page, size));
        } else if (minPrice != null && maxPrice != null) {
            products = productService.getProductsByPriceRange(minPrice, maxPrice, PageRequest.of(page, size));
        } else {
            products = productService.getAllProducts(PageRequest.of(page, size));
        }
        return ResponseEntity.ok(products);
    }


    @GetMapping("/mine")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProductsBySeller(user.getId(), PageRequest.of(page, size)));
    }


    @PostMapping("/estimate-price")
    public ResponseEntity<PriceEstimateResponse> estimatePrice(@RequestBody PriceEstimateRequest request) {
        return ResponseEntity.ok(pricingService.estimatePrice(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal User user,
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        productService.deleteProduct(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    @PatchMapping("/{id}/discount")
    public ResponseEntity<ProductResponse> applyDiscount(@PathVariable Long id, @RequestParam Double discount) {
        return ResponseEntity.ok(productService.applyDiscount(id, discount));
    }
}
