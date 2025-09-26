package com.example.demo.dto.response;

import com.example.demo.dto.ArticleWithCommentsDto;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        String email,
        String nickname,
        LocalDateTime createdAt,
        Set<ArticleCommentResponse> articleCommentResponses
) {
    public static ArticleWithCommentResponse of(Long id, String title, String content, String hashtag, String email, String nickname,
                                      LocalDateTime createdAt, Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentResponse(id, title, content, hashtag, email, nickname, createdAt, articleCommentResponses);
    }

    public static ArticleWithCommentResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentResponse(dto.id(), dto.title(), dto.content(), dto.hashtag(), dto.userAccountDto().email(), nickname,
                dto.createdAt(), dto.articleCommentDtos().stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }
}
