package com._3d.marketplace.entity.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer rating;
    private String comment;
}
