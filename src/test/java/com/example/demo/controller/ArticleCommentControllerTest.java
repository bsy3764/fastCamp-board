package com.example.demo.controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.request.ArticleCommentRequest;
import com.example.demo.service.ArticleCommentService;
import com.example.demo.service.ArticleService;
import com.example.demo.service.PaginationService;
import com.example.demo.util.FormDataEncoder;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

@DisplayName("뷰 컨트롤러 - 댓글")
@Import({SecurityConfig.class, FormDataEncoder.class})   // 테스트시 로그인안해서 로그인 없이 테스트 하기 위해
@WebMvcTest(ArticleCommentController.class) // 지정한 컨트롤러만 로딩해서 테스트
class ArticleCommentControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @Autowired
    private ArticleCommentService articleCommentService;

    public ArticleCommentControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][post] 댓글 등록 - 정상 호출")
    @Test
    void saveNewArticleComment() throws Exception {
        // given
        long articleId = 1L;
        ArticleCommentRequest comment = ArticleCommentRequest.of(articleId, "test comment");
        BDDMockito.willDoNothing().given(articleCommentService).saveArticleComment(any(ArticleCommentDto.class));

        // when
        mvc.perform(
                        MockMvcRequestBuilders.post("/comments/new")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(comment))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/articles/" + articleId))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/articles/" + articleId));
        BDDMockito.then(articleCommentService).should().saveArticleComment(any(ArticleCommentDto.class));

        // then
    }

    @DisplayName("[view][GET] 댓글 삭제 - 정상 호출")
    @Test
    void deleteArticleComment() throws Exception {
        // given
        long articleId = 1L;
        long articleCommentId = 1L;
        BDDMockito.willDoNothing().given(articleCommentService).deleteArticleComment(articleCommentId);

        // when
        mvc.perform(
                        MockMvcRequestBuilders.post("/comments/" + articleCommentId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/articles/" + articleId))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/articles/" + articleId));
        BDDMockito.then(articleCommentService).should().deleteArticleComment(articleCommentId);

        // then
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ArticleCommentService articleCommentService() {
            return Mockito.mock(ArticleCommentService.class);
        }
    }
}