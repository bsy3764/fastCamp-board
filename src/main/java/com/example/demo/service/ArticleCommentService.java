package com.example.demo.service;

import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.ArticleCommentUpdateDto;
import com.example.demo.dto.ArticleUpdateDto;
import com.example.demo.repository.ArticleCommentRepository;
import com.example.demo.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return List.of();
    }

    public void saveArticleComment(ArticleCommentDto dto) {

    }


    public void updateArticleComment(Long articleCommentId, ArticleCommentUpdateDto dto) {

    }

    public void deleteArticleComment(Long articleCommentId) {

    }
}
