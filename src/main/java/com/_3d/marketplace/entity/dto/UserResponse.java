package com._3d.marketplace.entity.dto;

import com._3d.marketplace.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private Set<Role> roles;
    private boolean active;
}
