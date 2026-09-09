package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.Material;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.DiscountRequest;
import com._3d.marketplace.entity.dto.PriceEstimateRequest;
import com._3d.marketplace.entity.dto.PriceEstimateResponse;
import com._3d.marketplace.entity.dto.ProductRequest;
import com._3d.marketplace.entity.dto.ProductResponse;
import com._3d.marketplace.entity.dto.StockRequest;
import com._3d.marketplace.exceptions.ProductNotFoundException;
import com._3d.marketplace.services.PricingService;
import com._3d.marketplace.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PricingService pricingService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {

        return ResponseEntity.ok(
                productService.searchProducts(name, categoryId, minPrice, maxPrice, pageable));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsBySeller(user.getId(), pageable));
    }

    @GetMapping("/materials")
    public ResponseEntity<List<Map<String, Object>>> getMaterials() {
        return ResponseEntity.ok(Arrays.stream(Material.values())
                .map(material -> Map.<String, Object>of(
                        "name", material.name(),
                        "pricePerKg", material.getPricePerKg()))
                .collect(Collectors.toList()));
    }

    @PostMapping("/estimate-price")
    public ResponseEntity<PriceEstimateResponse> estimatePrice(@RequestBody PriceEstimateRequest request) {
        return ResponseEntity.ok(pricingService.estimatePrice(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("No se encontró el producto con el id: " + id)));
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
    public ResponseEntity<ProductResponse> updateStock(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody StockRequest request) {
        return ResponseEntity.ok(productService.updateStock(id, request.getQuantity(), user));
    }

    @PatchMapping("/{id}/discount")
    public ResponseEntity<ProductResponse> applyDiscount(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(productService.applyDiscount(id, request.getDiscount(), user));
    }

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {

        ProductResponse response = productService.addImageToProduct(productId, file, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ProductResponse> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.deleteProductImage(productId, imageId, user));
    }
}
