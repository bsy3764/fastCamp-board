package com.example.demo.controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.service.ArticleService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@DisplayName("뷰 컨트롤러 - 게시글")
@Import(SecurityConfig.class)   // 테스트시 로그인안해서 로그인 없이 테스트 하기 위해
@WebMvcTest(ArticleController.class) // 지정한 컨트롤러만 로딩해서 테스트
class ArticleControllerTest {

    private final MockMvc mvc;
    @Autowired
    private ArticleService articleService;

    // ArticleService를 테스트에서 배제하기 위해
    // MockMvc가 api의 입출력만 테스트하기 위해
//    @MockBean <- deprecated 되므로 맨 아래 MockConfig 추가
//    private ArticleService service;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("게시판 리스트 페이지")
    @Test
    void getMethodArticleListTest() throws Exception {
        // given
        BDDMockito.given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class)))
                .willReturn(Page.empty());

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles"));
        BDDMockito.then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
    }

    @DisplayName("게시글 페이지")
    @Test
    void getMethodArticleTest() throws Exception {
        // given
        long articleId = 1L;
        BDDMockito.given(articleService.searchArticle(articleId))
                .willReturn(createArticleWithCommentsDto());

        // then
        mvc.perform(MockMvcRequestBuilders.get("/articles/" + articleId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/detail"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("article"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("articleComments"));
        BDDMockito.then(articleService).should().searchArticle(articleId);
    }

    @Disabled
    @DisplayName("게시글 검색 전용 페이지")
    @Test
    void getMethodArticleSearchTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/articles/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles/search"));
    }

    @Disabled
    @DisplayName("게시글 해시태그 검색 페이지")
    @Test
    void getMethodArticleHashtagSearchTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/articles/search-hashtag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles/search-hashtag"));
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ArticleService articleService() {
            return Mockito.mock(ArticleService.class);
        }
    }

    // Fixture 작성
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

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
}