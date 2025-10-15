package com.example.demo.controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.domain.type.SearchType;
import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.service.ArticleService;
import com.example.demo.service.PaginationService;
import com.example.demo.util.FormDataEncoder;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;

@DisplayName("뷰 컨트롤러 - 게시글")
@Import({SecurityConfig.class, FormDataEncoder.class})   // 테스트시 로그인안해서 로그인 없이 테스트 하기 위해
@WebMvcTest(ArticleController.class) // 지정한 컨트롤러만 로딩해서 테스트
class ArticleControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private PaginationService paginationService;
    @Autowired
    private View view;

    // ArticleService를 테스트에서 배제하기 위해
    // MockMvc가 api의 입출력만 테스트하기 위해
//    @MockBean <- deprecated 되므로 맨 아래 MockConfig 추가
//    private ArticleService service;

    public ArticleControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    void getMethodArticleListTest() throws Exception {
        // given
        BDDMockito.given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class)))
                .willReturn(Page.empty());
        BDDMockito.given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("paginationBarNumbers"));
        BDDMockito.then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void getArticleList_pagingAndSort_Test() throws Exception {
        // given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        BDDMockito.given(articleService.searchArticles(null, null, pageable))
                .willReturn(Page.empty());
        BDDMockito.given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages()))
                .willReturn(barNumbers);

        // when & then
        mvc.perform(
                MockMvcRequestBuilders.get("/articles")
                        .queryParam("Page", String.valueOf(pageNumber))
                        .queryParam("size", String.valueOf(pageSize))
                        .queryParam("sort", sortName + "," + direction)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles"))
                .andExpect(MockMvcResultMatchers.model().attribute("paginationBarNumbers", barNumbers));
        BDDMockito.then(articleService).should().searchArticles(null, null, pageable);
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 검색어와 함께 호출")
    @Test
    void getArticleList_pagingAndKeyword_Test() throws Exception {
        // given
        SearchType searchType = SearchType.TITLE;
        String keyword = "title";
        BDDMockito.given(articleService.searchArticles(eq(searchType), eq(keyword), any(Pageable.class)))
                .willReturn(Page.empty());
        BDDMockito.given(paginationService.getPaginationBarNumbers(anyInt(), anyInt()))
                .willReturn(List.of(0,1,2,3,4));

        // when & then
        mvc.perform(
                        MockMvcRequestBuilders.get("/articles")
                                .queryParam("searchType", searchType.name())
                                .queryParam("keyword", keyword)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("searchTypes"));
        BDDMockito.then(articleService).should().searchArticles(eq(searchType), eq(keyword), any(Pageable.class));
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 페이지 - 정상 호출")
    @Test
    void getMethodArticleTest() throws Exception {
        // given
        long articleId = 1L;
        BDDMockito.given(articleService.getArticleWithComments(articleId))
                .willReturn(createArticleWithCommentsDto());

        // then
        mvc.perform(MockMvcRequestBuilders.get("/articles/" + articleId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/detail"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("article"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("articleComments"));
        BDDMockito.then(articleService).should().getArticleWithComments(articleId);
    }

    @Disabled
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    void getMethodArticleSearchTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/articles/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/search"));
    }

    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")

    @Test
    void getMethodArticle_NoHashtagSearchTest() throws Exception {
        // given
        BDDMockito.given(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class)))
                .willReturn(Page.empty());
        BDDMockito.given(paginationService.getPaginationBarNumbers(anyInt(), anyInt()))
                .willReturn(List.of(1,2,3,4,5));
        BDDMockito.given(articleService.getHashtags()).willReturn(List.of("#java", "#spring", "#boot"));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/articles/search-hashtag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/search-hashtag"))
                .andExpect(MockMvcResultMatchers.model().attribute("articles", Page.empty()))
                .andExpect(MockMvcResultMatchers.model().attributeExists("hashtags"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("paginationBarNumbers"));
        BDDMockito.then(articleService).should().searchArticlesViaHashtag(eq(null), any(Pageable.class));
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
        BDDMockito.then(articleService).should().getHashtags();
    }

    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출, 해시태그 입력")
    @Test
    void getMethodArticleHashtagSearchTest() throws Exception {
        // given
        String hashtag = "#java";
        List<String> hashtags = List.of("#java", "#spring", "#boot");
        BDDMockito.given(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class)))
                .willReturn(Page.empty());
        BDDMockito.given(paginationService.getPaginationBarNumbers(anyInt(), anyInt()))
                .willReturn(List.of(1,2,3,4,5));
        BDDMockito.given(articleService.getHashtags()).willReturn(hashtags);

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/articles/search-hashtag").queryParam("keyword", hashtag))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.view().name("articles/search-hashtag"))
                .andExpect(MockMvcResultMatchers.model().attribute("articles", Page.empty()))
                .andExpect(MockMvcResultMatchers.model().attribute("searchType", SearchType.HASHTAG))
                .andExpect(MockMvcResultMatchers.model().attribute("hashtags", hashtags))
                .andExpect(MockMvcResultMatchers.model().attributeExists("paginationBarNumbers"));
        BDDMockito.then(articleService).should().searchArticlesViaHashtag(eq(hashtag), any(Pageable.class));
        BDDMockito.then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
        BDDMockito.then(articleService).should().getHashtags();
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ArticleService articleService() {
            return Mockito.mock(ArticleService.class);
        }

        @Bean
        public PaginationService paginationService() {
            return Mockito.mock(PaginationService.class);
        }
    }

    // Fixture 작성
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