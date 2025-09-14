package com.example.demo.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Disabled("spring data rest api 테스트 한 후로는 필요 없어 제외시킴")
@DisplayName("spring data rest api 테스트")    // api 존재 여부만 빠르게 확인
@Transactional  // 테스트 끝나고 롤백
@AutoConfigureMockMvc   // @SpringBootTest 만으론 MockMvc 를 인지 못해서 추가해줌
@SpringBootTest // 통합 테스트
// @WebMvcTest // MockMvc 를 사용할 수 있게 해줌, 슬라이스 테스트, 컨트롤러 빈 외의 빈들을 로드하지 않음
public class DataRestTest {

    private final MockMvc mvc;

    public DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("게시글 리스트 조회")
    @Test
    void articleListSelect() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("application/hal+json")));
                //.andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시글 단건 조회")
    @Test
    void articleSelect() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/articles/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("application/hal+json")));
        //.andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("게시글에 달린 댓글 리스트 조회")
    @Test
    void commentListFromArticleSelect() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/articles/1/articleComments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("application/hal+json")));
        //.andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("댓글 리스트 조회")
    @Test
    void commentListSelect() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/articleComments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("application/hal+json")));
        //.andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("댓글 단건 조회")
    @Test
    void commentSelect() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/articleComments/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.valueOf("application/hal+json")));
        //.andDo(MockMvcResultHandlers.print());
    }
}
