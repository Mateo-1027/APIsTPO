package com._3d.marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private float price;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
