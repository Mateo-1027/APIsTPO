package com._3d.marketplace.services;

import com._3d.marketplace.entity.Product;
import com._3d.marketplace.entity.Review;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.ReviewRequest;
import com._3d.marketplace.entity.dto.ReviewResponse;
import com._3d.marketplace.exceptions.ProductNotFoundException;
import com._3d.marketplace.exceptions.ReviewNotAllowedException;
import com._3d.marketplace.exceptions.UserNotFoundException;
import com._3d.marketplace.repositories.OrderRepository;
import com._3d.marketplace.repositories.ProductRepository;
import com._3d.marketplace.repositories.ReviewRepository;
import com._3d.marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByDateDesc(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ReviewResponse> getSellerReviews(Long sellerId) {
        return reviewRepository.findBySellerIdOrderByDateDesc(sellerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponse createProductReview(Long productId, ReviewRequest request, User author) {
        validate(request);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("No se encontró el producto con el id: " + productId));

        if (!orderRepository.existsByUserIdAndItems_Product_Id(author.getId(), productId)) {
            throw new ReviewNotAllowedException("Solo podés reseñar productos que compraste.");
        }
        if (reviewRepository.existsByAuthorIdAndProductId(author.getId(), productId)) {
            throw new ReviewNotAllowedException("Ya dejaste una reseña para este producto.");
        }

        Review review = newReview(request, author);
        review.setProduct(product);
        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewResponse createSellerReview(Long sellerId, ReviewRequest request, User author) {
        validate(request);
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el vendedor con el id: " + sellerId));

        if (seller.getId().equals(author.getId())) {
            throw new ReviewNotAllowedException("No podés reseñarte a vos mismo.");
        }
        if (!orderRepository.existsByUserIdAndItems_Product_Seller_Id(author.getId(), sellerId)) {
            throw new ReviewNotAllowedException("Solo podés reseñar vendedores a los que les compraste.");
        }
        if (reviewRepository.existsByAuthorIdAndSellerId(author.getId(), sellerId)) {
            throw new ReviewNotAllowedException("Ya dejaste una reseña para este vendedor.");
        }

        Review review = newReview(request, author);
        review.setSeller(seller);
        return mapToResponse(reviewRepository.save(review));
    }

    private void validate(ReviewRequest request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("La calificación tiene que ser de 1 a 5 estrellas.");
        }
        if (request.getComment() != null && request.getComment().length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede superar los 1000 caracteres.");
        }
    }

    private Review newReview(ReviewRequest request, User author) {
        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment() == null ? "" : request.getComment().trim());
        review.setDate(LocalDateTime.now());
        review.setAuthor(author);
        return review;
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setDate(review.getDate());
        response.setAuthorName(review.getAuthor().getName());
        return response;
    }
}
