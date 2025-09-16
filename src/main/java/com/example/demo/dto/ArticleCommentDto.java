package com.example.demo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.demo.domain.ArticleComment}
 */
public record ArticleCommentDto(
        LocalDateTime createdAt,
        String createdBy,
        String content
) {
    public static ArticleCommentDto of(LocalDateTime createdAt, String createdBy,String content) {
        return new ArticleCommentDto(createdAt, createdBy, content);
    }
}