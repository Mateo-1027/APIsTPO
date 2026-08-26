package com._3d.marketplace.services;

import com._3d.marketplace.entity.Category;
import com._3d.marketplace.entity.Product;
import com._3d.marketplace.entity.ProductImage;
import com._3d.marketplace.entity.dto.ProductRequest;
import com._3d.marketplace.entity.dto.ProductResponse;
import com._3d.marketplace.exceptions.ProductNotFoundException;
import com._3d.marketplace.repositories.CategoryRepository;
import com._3d.marketplace.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable).map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("No se encontró el producto con el id: " + id));
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        mapToEntity(request, product);
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("No se encontró el producto con el id: " + id));
        mapToEntity(request, product);
        return mapToResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("No se encontró el producto con el id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("No se encontró el producto con el id: " + id));
        product.setStock(product.getStock() + quantity);
        return mapToResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse applyDiscount(Long id, Double discount) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("No se encontró el producto con el id: " + id));
        product.setDiscount(discount);
        return mapToResponse(productRepository.save(product));
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setDiscount(product.getDiscount());
        if (product.getCategory() != null) {
            response.setCategoryName(product.getCategory().getDescription());
        }
        if (product.getImages() != null) {
            response.setImageUrls(product.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList()));
        } else {
            response.setImageUrls(new ArrayList<>());
        }
        return response;
    }

    private void mapToEntity(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        if (request.getDiscount() != null) {
            product.setDiscount(request.getDiscount());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("La categoría no existe"));
            product.setCategory(category);
        }
        
        if (request.getImageUrls() != null) {
            if (product.getImages() == null) {
                product.setImages(new ArrayList<>());
            } else {
                product.getImages().clear();
            }
            for (String url : request.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setUrl(url);
                img.setProduct(product);
                product.getImages().add(img);
            }
        }
    }
}
