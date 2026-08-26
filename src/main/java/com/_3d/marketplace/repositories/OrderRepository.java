package com._3d.marketplace.repositories;

import com._3d.marketplace.entity.Order;
import com._3d.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByDateDesc(User user);
}
