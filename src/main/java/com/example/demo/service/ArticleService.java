package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.type.SearchType;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository repository;

    @Transactional(readOnly = true) // 조회하는 것이라 read-only 적용
    // Page를 사용해서 페이지네이션과 정렬 기능이 포함됨
    public Page<ArticleDto> searchArticles(SearchType searchType, String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE -> repository.findByTitleContaining(keyword, pageable).map(ArticleDto::from);
            case CONTENT -> repository.findByContentContaining(keyword, pageable).map(ArticleDto::from);
            case ID -> repository.findByUserAccount_UserIdContaining(keyword, pageable).map(ArticleDto::from);
            case NICKNAME -> repository.findByUserAccount_NicknameContaining(keyword, pageable).map(ArticleDto::from);
            case HASHTAG -> repository.findByHashtag("#" + keyword, pageable).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto searchArticle(Long articleId) {
        return repository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다"));
    }

    public void saveArticle(ArticleDto dto) {
        repository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDto dto) {
        try {
            Article article = repository.getReferenceById(dto.id());
            if (dto.title() != null) article.setTitle(dto.title());
            if (dto.content() != null) article.setContent(dto.content());
            article.setHashtag(dto.hashtag());
//         repository.save(article);  <- 영속성 컨텍스트로 인해 변화를 감지해 자동으로 save함, 코드가 있어도 없어도 됨
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패, 게시글을 찾을 수 없음: {}", dto);
            // 문자 보간방법(String Interpolation)
        }
    }

    public void deleteArticle(Long articleId) {
        repository.deleteById(articleId);
    }
}
