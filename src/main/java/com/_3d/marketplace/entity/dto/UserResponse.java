package com._3d.marketplace.entity.dto;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private Set<Role> roles;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setSurname(user.getSurname());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        return response;
    }
}
