package com._3d.marketplace.services;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.ReviewRequest;
import com._3d.marketplace.entity.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> getProductReviews(Long productId);
    List<ReviewResponse> getSellerReviews(Long sellerId);
    ReviewResponse createProductReview(Long productId, ReviewRequest request, User author);
    ReviewResponse createSellerReview(Long sellerId, ReviewRequest request, User author);
    void deleteReview(Long reviewId, User user);
}
