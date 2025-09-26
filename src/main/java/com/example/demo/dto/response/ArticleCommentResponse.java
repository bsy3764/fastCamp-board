package com.example.demo.dto.response;

import com.example.demo.dto.ArticleCommentDto;

import java.time.LocalDateTime;

public record ArticleCommentResponse(
        Long id,
        String content,
        String email,
        String nickname,
        LocalDateTime createdAt
) {
    public static ArticleCommentResponse of(Long id, String content, String email, String nickname, LocalDateTime createdAt) {
        return new ArticleCommentResponse(id, content, email, nickname, createdAt);
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleCommentResponse(dto.id(), dto.content(), dto.userAccountDto().email(), nickname, dto.createdAt());
    }
}
