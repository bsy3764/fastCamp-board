package com.example.demo.repository;

import com.example.demo.config.JpaConfig;
import com.example.demo.domain.Article;
import com.example.demo.domain.ArticleComment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JPA 연결 테스트")
// JpaConfig로 직접 @Configuration 한 부분 인지하도록 import 추가
// @EnableJpaAuditing 한 부분한 부분 인지하기 위해
@Import(JpaConfig.class)
@DataJpaTest    // 메서드 단위로 자동으로 트랜잭션이 걸럼, 테스트는 기본이 롤백
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @BeforeEach
    void setInitData() throws IOException {
        // 테스트데이터를 대량으로 생성해주는 서비스 사용(mockaroo 웹서비스)에서 sql 가져오기
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/data.csv"), StandardCharsets.UTF_8))) {
            reader.lines().skip(1).forEach(line -> {
                String[] sql = line.split(",");
                Article article = Article.of(sql[0], sql[1], sql[2]);
//                System.out.println("article = " + article);
                articleRepository.save(article);
            });
        }
    }

    @DisplayName("select test")
    @Test
    void selectTest() throws IOException {
        // given
        // 테스트데이터를 대량으로 생성해주는 서비스 사용(mockaroo 웹서비스)에서 sql 가져오기
//        try (BufferedReader reader = new BufferedReader(
//                                        new InputStreamReader(getClass().getResourceAsStream("/data.csv"), StandardCharsets.UTF_8))) {
//            reader.lines().skip(1).forEach(line -> {
//                String[] sql = line.split(",");
//                Article article = Article.of(sql[0], sql[1], sql[2]);
////                System.out.println("article = " + article);
//                articleRepository.save(article);
//            });
//        }

        // when
        List<Article> articles = articleRepository.findAll();

        // then
        assertThat(articles)
                .isNotNull()
                .hasSize(1000);
    }

    @DisplayName("insert test")
    @Test
    void insertTest() {
        // given
        long previousCount = articleRepository.count();
        Article article = Article.of("new Article", "hello test", "#spring");

        // when
        Article savedArticle = articleRepository.save(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update test")
    @Test
    void updateTest() {
        // given
//        Article article = articleRepository.findById(1L).orElseThrow();
        Article article = articleRepository.findAll().get(0);
        String updateHashtag = "#jpa";
        article.setHashtag(updateHashtag);

        // when
        Article savedArticle = articleRepository.saveAndFlush(article);

        // then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updateHashtag);
    }

    @DisplayName("delete test")
    @Test
    void deleteTest() throws IOException {
        // 테스트데이터를 대량으로 생성해주는 서비스 사용(mockaroo 웹서비스)에서 sql 가져오기
        try (BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(getClass().getResourceAsStream("/comment_data.csv"), StandardCharsets.UTF_8))) {
            reader.lines().skip(1).forEach(line -> {
                String[] sql = line.split(",");
                Long articleId = Long.parseLong(sql[0]);

                Article article = articleRepository.findById(articleId)
                        .orElse(null);
                if (article != null) {
                    ArticleComment comment = ArticleComment.of(sql[1], article);
                    articleCommentRepository.save(comment);
                }
            });
        }

        // given
//        Article article = articleRepository.findById(1L).orElseThrow();
        Article article = articleRepository.findAll().get(0);
        long previousCount = articleRepository.count();

        // 연관관계 매핑을 통해 cascade 옵션으로 게시글을 삭제하면 댓글도 전부 삭제되는지 확인
        long previousCommentCount = articleCommentRepository.count();
        int deleteCommentSize = article.getArticleComments().size();

        // when
        articleRepository.delete(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousCommentCount - deleteCommentSize);
    }

}