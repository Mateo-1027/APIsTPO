package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.ReviewRequest;
import com._3d.marketplace.entity.dto.ReviewResponse;
import com._3d.marketplace.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/products/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<List<ReviewResponse>> getSellerReviews(@PathVariable Long sellerId) {
        return ResponseEntity.ok(reviewService.getSellerReviews(sellerId));
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ReviewResponse> createProductReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createProductReview(productId, request, user));
    }

    @PostMapping("/sellers/{sellerId}")
    public ResponseEntity<ReviewResponse> createSellerReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long sellerId,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createSellerReview(sellerId, request, user));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.noContent().build();
    }
}
