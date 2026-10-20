package com._3d.marketplace.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime date;
    private Long authorId;
    private String authorName;
}
