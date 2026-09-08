package com._3d.marketplace.services;

import com._3d.marketplace.entity.Category;
import com._3d.marketplace.entity.Product;
import com._3d.marketplace.entity.ProductImage;
import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.ProductRequest;
import com._3d.marketplace.entity.dto.ProductResponse;
import com._3d.marketplace.exceptions.ForbiddenOperationException;
import com._3d.marketplace.exceptions.CategoryNotFoundException;
import com._3d.marketplace.exceptions.ProductNotFoundException;
import com._3d.marketplace.repositories.CategoryRepository;
import com._3d.marketplace.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public Page<ProductResponse> searchProducts(String name, Long categoryId, Double minPrice, Double maxPrice,
            Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> filters = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                filters.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                filters.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                filters.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                filters.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            filters.add(cb.isTrue(root.get("active")));
            return cb.and(filters.toArray(new Predicate[0]));
        };
        return productRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<ProductResponse> getProductsBySeller(Long sellerId, Pageable pageable) {
        return productRepository.findBySellerIdAndActiveTrue(sellerId, pageable).map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = findActive(id);
        return mapToResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request, User seller) {
        Product product = new Product();
        mapToEntity(request, product);
        product.setSeller(seller);
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request, User user) {
        Product product = findActive(id);
        checkOwnership(product, user);
        mapToEntity(request, product);
        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id, User user) {
        Product product = findActive(id);
        checkOwnership(product, user);
        product.setActive(false);
        productRepository.save(product);
    }

    private Product findActive(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("No se encontró el producto con el id: " + id));
    }

    private void checkOwnership(Product product, User user) {
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);
        boolean isOwner = product.getSeller() != null
                && product.getSeller().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new ForbiddenOperationException(
                    "No tenés permiso para modificar un producto que no publicaste.");
        }
    }

    @Override
    public ProductResponse updateStock(Long id, Integer quantity, User user) {
        Product product = findActive(id);
        checkOwnership(product, user);
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("El stock no puede quedar negativo. Stock actual: " + product.getStock());
        }
        product.setStock(newStock);
        return mapToResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse applyDiscount(Long id, Double discount, User user) {
        Product product = findActive(id);
        checkOwnership(product, user);
        validateDiscount(discount);
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
        if (product.getSeller() != null) {
            response.setSellerId(product.getSeller().getId());
            response.setSellerName(product.getSeller().getName());
        }
        if (product.getImages() != null) {
            response.setImageUrls(product.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList()));
        } else {
            response.setImageUrls(new ArrayList<>());
        }
        return response;
    }

    private void validateDiscount(Double discount) {
        if (discount == null || discount < 0 || discount > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100.");
        }
    }

    private void mapToEntity(ProductRequest request, Product product) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (request.getPrice() == null || request.getPrice() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        if (request.getDiscount() != null) {
            validateDiscount(request.getDiscount());
            product.setDiscount(request.getDiscount());
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        Category category = categoryRepository.findByIdAndActiveTrue(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("La categoría no existe: " + request.getCategoryId()));
        product.setCategory(category);

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

    @Override
    public ProductResponse addImageToProduct(Long productId, MultipartFile file, User user) throws IOException {
        Product product = findActive(productId);

        checkOwnership(product, user);

        Map<?, ?> uploaded = cloudinaryService.uploadFile(file);

        ProductImage productImage = new ProductImage();
        productImage.setUrl(uploaded.get("secure_url").toString());
        productImage.setPublicId(uploaded.get("public_id").toString());
        productImage.setProduct(product);

        product.getImages().add(productImage);
        productRepository.save(product);

        return mapToResponse(product);
    }

    @Override
    public ProductResponse deleteProductImage(Long productId, Long imageId, User user) {
        Product product = findActive(productId);

        checkOwnership(product, user);

        ProductImage image = product.getImages().stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(
                        "El producto no tiene una imagen con el id: " + imageId));

        product.getImages().remove(image);
        ProductResponse response = mapToResponse(productRepository.save(product));

        if (image.getPublicId() != null) {
            cloudinaryService.delete(image.getPublicId());
        }
        return response;
    }
}
