package com.example.demo.service;

import com.example.demo.domain.type.SearchType;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleUpdateDto;
import com.example.demo.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository repository;

    @Transactional(readOnly = true) // 조회하는 것이라 read-only 적용
    // Page를 사용해서 페이지네이션과 정렬 기능이 포함됨
    public Page<ArticleDto> searchArticles(SearchType searchType, String keyword) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ArticleDto searchArticle(Long articleId) {
        return ArticleDto.of(LocalDateTime.now(), "uno", "title", "content", "#java");
    }

    public void saveArticle(ArticleDto dto) {

    }

    public void updateArticle(Long articleId, ArticleUpdateDto dto) {

    }

    public void deleteArticle(Long articleId) {

    }
}
