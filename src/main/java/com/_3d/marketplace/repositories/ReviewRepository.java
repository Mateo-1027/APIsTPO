package com._3d.marketplace.repositories;

import com._3d.marketplace.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByDateDesc(Long productId);

    List<Review> findBySellerIdOrderByDateDesc(Long sellerId);

    boolean existsByAuthorIdAndProductId(Long authorId, Long productId);

    boolean existsByAuthorIdAndSellerId(Long authorId, Long sellerId);
}
