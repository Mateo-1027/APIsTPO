package com._3d.marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column String email;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

}
