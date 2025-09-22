package com.example.demo.repository;

import com.example.demo.config.JpaConfig;
import com.example.demo.domain.Article;
import com.example.demo.domain.ArticleComment;
import com.example.demo.domain.UserAccount;
import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.UserAccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// application.yml에서 spring.profiles로 프로필을 설정
@ActiveProfiles("test")   // 설정한 프로필을 활성화
@DisplayName("JPA Conn test")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository,
            @Autowired UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Test
    void selectTest() {
        // given

        // when
        List<Article> articles = articleRepository.findAll();

        // then
        assertThat(articles).isNotNull().hasSize(11);
    }

    @Test
    void insertTest() {
        // given
        long previousCount = articleRepository.count();
        Article article = createArticle();

        // when
        Article saved = articleRepository.save(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @Test
    void updateTest() {
        // given
        Article article = createArticle();
        Article saved = articleRepository.save(article);
        String updatedHashtag = "#springboot";
        saved.setHashtag(updatedHashtag);

        // when
        Article updated = articleRepository.saveAndFlush(article);

        // then
        assertThat(updated).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @Test
    void deleteTest() {
        // given
        UserAccount userAccount = userAccountRepository.save(createUserAccount());
        Article article = createArticle();

        // 댓글을 Article에 직접 추가해서 연관관계 주입
        ArticleComment comment1 = ArticleComment.of(article, userAccount,"comment1");
        ArticleComment comment2 = ArticleComment.of(article, userAccount,"comment2");

        // 연관관계 수동 설정 (양방향을 article -> comment, comment -> article 양쪽 연결)
        article.getArticleComments().add(comment1);
        article.getArticleComments().add(comment2);

        // cascade = CascadeType.ALL 이므로 article 저장만으로 댓글도 함께 저장됨
        Article saved = articleRepository.saveAndFlush(article);

        long previousArticleCount = articleRepository.count();
        long previousCommentCount = articleCommentRepository.count();
        int deletedCommentSize = saved.getArticleComments().size(); // 저장된 객체에서 사이즈 가져오기

        // when
        articleRepository.delete(saved);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousCommentCount - deletedCommentSize);
    }

    // Fixture 작성
    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                content,
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
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

    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
                Article.of(createUserAccount(), "title", "content", "hashtag"),
                createUserAccount(),
                content
        );
    }

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
        UserAccount savedUser = userAccountRepository.save(createUserAccount());

        return Article.of(
                savedUser,
                "title",
                "content",
                "#java"
        );
    }

}