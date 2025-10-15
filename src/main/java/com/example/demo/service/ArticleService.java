package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.UserAccount;
import com.example.demo.domain.type.SearchType;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository repository;
    private final UserAccountRepository userAccountRepository;

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
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return repository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다"));
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return repository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다"));
    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        repository.save(dto.toEntity(userAccount));
    }

    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = repository.getReferenceById(articleId);
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

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }
        return repository.findByHashtag(keyword, pageable).map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return repository.findALlDistinctHashtags();
    }

}
