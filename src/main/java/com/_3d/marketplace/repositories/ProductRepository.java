package com._3d.marketplace.repositories;

import com._3d.marketplace.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findBySellerIdAndActiveTrue(Long sellerId, Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(Long id);

    @Modifying
    @Query("update Product p set p.active = false where p.seller.id = :sellerId and p.active = true")
    int deactivateBySellerId(@Param("sellerId") Long sellerId);
}
