package com._3d.marketplace.entity.dto;

import lombok.Data;

@Data
public class EmailChangeRequest {
    private String newEmail;
    private String currentPassword;
}
