package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.UserAccount;
import com.example.demo.domain.type.SearchType;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DisplayName("게시글 테스트")
@ExtendWith(MockitoExtension.class) // mockito 사용
class ArticleServiceTest {

    @InjectMocks // mock을 주입하는 객체
    private ArticleService sut;

    @Mock   // @InjectMocks 이외의 객체들에 붙임
    private ArticleRepository articleRepository;

    @DisplayName("게시글 검색 테스트 - 검색어 없이 검색하면 게시글 페이지 반환")
    @Test
    void searchArticlesNoKeyword() {
        // given
        Pageable pageable = Pageable.ofSize(20);
        BDDMockito.given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // when
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // then
        assertThat(articles).isEmpty();
        BDDMockito.then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("게시글 검색 테스트 - 검색어 검색")
    @Test
    void searchingArticlesUseKeyword() {
        // given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        BDDMockito.given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // when
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // then
        assertThat(articles).isEmpty();
        BDDMockito.then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("게시글 검색 테스트 - keyword 없이 해시태그 검색")
    @Test
    void searchingArticlesUseHashtag_noKeyword() {
        // given
        Pageable pageable = Pageable.ofSize(20);

        // when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);

        // then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        BDDMockito.then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("게시글 검색 테스트 - 해시태그 검색")
    @Test
    void searchingArticlesUseHashtag() {
        // given
        String hashtag = "#java";
        Pageable pageable = Pageable.ofSize(20);
        BDDMockito.given(articleRepository.findByHashtag(hashtag, pageable))
                .willReturn(Page.empty(pageable));

        // when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtag, pageable);

        // then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        BDDMockito.then(articleRepository).should().findByHashtag(hashtag, pageable);
    }

    @DisplayName("해시태그 리스트")
    @Test
    void getHashtagList() {
        // given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        BDDMockito.given(articleRepository.findALlDistinctHashtags())
                .willReturn(expectedHashtags);

        // when
        List<String> actualHashtags = sut.getHashtags();

        // then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        BDDMockito.then(articleRepository).should().findALlDistinctHashtags();
    }

    @DisplayName("게시글 검색 테스트")
    @Test
    void searchingArticle() {
        // given
        Long articleId = 1L;
        Article article = createArticle();
        articleRepository.saveAndFlush(article);
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        ArticleWithCommentsDto dto = sut.searchArticle(articleId);

        // then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글 조회")
    @Test
    void searchNotExistArticle() {
        // given
        Long articleId = 0L;
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> sut.searchArticle(articleId));

        // then
        assertThat(throwable).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다");
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 생성")
    @Test
    void insertArticle() {
        // given
        ArticleDto dto = createArticleDto();

        // repository에서 save() 가 호출될 것
        BDDMockito.given(articleRepository.save(ArgumentMatchers.any(Article.class)))
                .willReturn(createArticle());

        // when
        sut.saveArticle(dto);

        // then
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(articleRepository).should().save(ArgumentMatchers.any(Article.class));
    }

    @DisplayName("게시글 수정")
    @Test
    void updateArticle() {
        // given
        ArticleDto articleDto = createArticleDto();

        // repository에서 save() 가 호출될 것
        BDDMockito.given(articleRepository.getReferenceById(articleDto.id()))
                .willThrow(EntityNotFoundException.class);

        // when
        sut.updateArticle(articleDto);

        // then
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(articleRepository).should().getReferenceById(articleDto.id());
    }

    @DisplayName("게시글 삭제")
    @Test
    void deleteArticle() {
        // given
        Long articleId = 1L;

        // repository에서 delete() 가 호출될 것
        BDDMockito.willDoNothing().given(articleRepository).deleteById(articleId);

        // when
        sut.deleteArticle(articleId);

        // then
        // repository에서 delete() 가 호출되었는지 확인
        BDDMockito.then(articleRepository).should().deleteById(articleId);
    }

    // Fixture 작성
    private UserAccount createUserAccount() {
        return UserAccount.of(
                "uno",
                "password",
                "uno@email.com",
                "Uno",
                null
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "Uno",
                LocalDateTime.now(),
                "Uno");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "uno",
                "password",
                "uno@mail.com",
                "Uno",
                "This is memo",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
}