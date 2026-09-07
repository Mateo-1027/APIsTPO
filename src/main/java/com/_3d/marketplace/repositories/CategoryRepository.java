package com._3d.marketplace.repositories;

import com._3d.marketplace.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "select c from Category c where c.description = ?1 and c.active = true")
    List<Category> findByDescription(String description);

    Page<Category> findByActiveTrue(Pageable pageable);

    Optional<Category> findByIdAndActiveTrue(Long id);
}
