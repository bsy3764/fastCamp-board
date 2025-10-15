package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.UserAccount;
import com.example.demo.domain.type.SearchType;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.UserAccountRepository;
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

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class) // mockito 사용
class ArticleServiceTest {

    @InjectMocks // mock을 주입하는 객체
    private ArticleService sut;

    @Mock   // @InjectMocks 이외의 객체들에 붙임
    private ArticleRepository articleRepository;

    @Mock   // @InjectMocks 이외의 객체들에 붙임
    private UserAccountRepository userAccountRepository;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환")
    @Test
    void searchArticles_noKeyword() {
        // given
        Pageable pageable = Pageable.ofSize(20);
        BDDMockito.given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // when
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // then
        assertThat(articles).isEmpty();
        BDDMockito.then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환")
    @Test
    void searchingArticles_useKeyword() {
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

    @DisplayName("검색어 없이 게시글을 해시태그 검색하면, 빈 페이지를 반환")
    @Test
    void searchingArticles_useHashtag_noKeyword() {
        // given
        Pageable pageable = Pageable.ofSize(20);

        // when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);

        // then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        BDDMockito.then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("게시글을 해시태그 검색하면, 게시글 페이지를 반환")
    @Test
    void searchingArticles_useHashtag() {
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

    @DisplayName("게시글Id로 조회하면, 댓글 달린 게시글을 반환")
    @Test
    void getArticleWithComments_byArticleId() {
        // given
        Long articleId = 1L;
        Article article = createArticle();
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        // then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 달린 게시글이 없으면 예외 던짐")
    @Test
    void getArticleWithComments_noComments() {
        // given
        Long articleId = 0L;
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        // then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다");
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("해시태그를 조회하면, 유니크 해시태그 리스트를 반환")
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

    @DisplayName("게시글을 조회하면, 게시글을 반환")
    @Test
    void searchingArticle() {
        // given
        Long articleId = 1L;
        Article article = createArticle();
        articleRepository.saveAndFlush(article);
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        ArticleDto dto = sut.getArticle(articleId);

        // then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글이 없으면, 예외를 던짐")
    @Test
    void searchArticle_notExist() {
        // given
        Long articleId = 0L;
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> sut.getArticle(articleId));

        // then
        assertThat(throwable).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다");
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성")
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

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정")
    @Test
    void updateArticle() {
        // given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        BDDMockito.given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        // repository에서 save() 가 호출될 것
        BDDMockito.given(articleRepository.getReferenceById(dto.id()))
                .willThrow(EntityNotFoundException.class);

        // when
        sut.updateArticle(dto.id(), dto);

        // then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제")
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