package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.type.SearchType;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleUpdateDto;
import com.example.demo.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("게시글 테스트")
@ExtendWith(MockitoExtension.class) // mockito 사용
class ArticleServiceTest {

    @InjectMocks // mock을 주입하는 객체
    private ArticleService sut;

    @Mock   // @InjectMocks 이외의 객체들에 붙임
    private ArticleRepository repository;

    @DisplayName("게시글 검색 테스트")
    @Test
    void searchingArticles() {
        // given

        // when
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "keyword");

        // then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글 검색 테스트")
    @Test
    void searchingArticle() {
        // given
        Long articleId = 1L;

        // when
        ArticleDto article = sut.searchArticle(articleId);

        // then
        assertThat(article).isNotNull();
    }

    @DisplayName("게시글 생성")
    @Test
    void createArticle() {
        // given
        ArticleDto dto = ArticleDto.of(LocalDateTime.now(), "uno", "title", "content", "#java");

        // repository에서 save() 가 호출될 것
        BDDMockito.given(repository.save(ArgumentMatchers.any(Article.class)))
                .willReturn(null);

        // when
        sut.saveArticle(dto);

        // then
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(repository).should().save(ArgumentMatchers.any(Article.class));
    }

    @DisplayName("게시글 수정")
    @Test
    void updateArticle() {
        // given
        Long articleId = 1L;
        ArticleUpdateDto dto = ArticleUpdateDto.of("title", "content", "#java");

        // repository에서 save() 가 호출될 것
        BDDMockito.given(repository.save(ArgumentMatchers.any(Article.class)))
                .willReturn(null);

        // when
        sut.updateArticle(articleId, dto);

        // then
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(repository).should().save(ArgumentMatchers.any(Article.class));
    }

    @DisplayName("게시글 삭제")
    @Test
    void deleteArticle() {
        // given
        Long articleId = 1L;

        // repository에서 delete() 가 호출될 것
        BDDMockito.willDoNothing().given(repository).delete(ArgumentMatchers.any(Article.class));

        // when
        sut.deleteArticle(articleId);

        // then
        // repository에서 delete() 가 호출되었는지 확인
        BDDMockito.then(repository).should().delete(ArgumentMatchers.any(Article.class));
    }

}