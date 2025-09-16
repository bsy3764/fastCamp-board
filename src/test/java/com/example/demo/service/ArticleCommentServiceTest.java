package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.ArticleComment;
import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.ArticleCommentUpdateDto;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleUpdateDto;
import com.example.demo.repository.ArticleCommentRepository;
import com.example.demo.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("댓글 테스트")
@ExtendWith(MockitoExtension.class) // mockito 사용
class ArticleCommentServiceTest {

    @InjectMocks
    private ArticleCommentService sut;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCommentRepository articleCommentRepository;

    @DisplayName("게시글ID로 댓글 조회")
    @Test
    void getCommentsByArticleId() {
        // given
        Long articleId = 1L;
        BDDMockito.given(articleRepository.findById(articleId)).willReturn(
                Optional.of(Article.of("title", "content", "#java")));

        // when
        List<ArticleCommentDto> comments = sut.searchArticleComments(articleId);

        // then
        assertThat(comments).isNotNull();
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 저장")
    @Test
    void createComment() {
        // given
        ArticleCommentDto dto = ArticleCommentDto.of(LocalDateTime.now(), "uno", "content");
        // repository에서 save() 가 호출될 것
        BDDMockito.given(articleCommentRepository.save(ArgumentMatchers.any(ArticleComment.class)))
                .willReturn(null);

        // when
        sut.saveArticleComment(dto);

        // then
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(articleCommentRepository).should().save(ArgumentMatchers.any(ArticleComment.class));
    }

    @DisplayName("댓글 수정")
    @Test
    void updateComment() {
        // given
        Long articleCommentId = 1L;
        ArticleCommentUpdateDto dto = ArticleCommentUpdateDto.of("content");

        // repository에서 save() 가 호출될 것
        BDDMockito.given(articleCommentRepository.save(ArgumentMatchers.any(ArticleComment.class)))
                .willReturn(null);

        // when
        sut.updateArticleComment(articleCommentId, dto);

        // then
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(articleCommentRepository).should().save(ArgumentMatchers.any(ArticleComment.class));
    }

    @DisplayName("댓글 삭제")
    @Test
    void deleteComment() {
        // given
        Long articleCommentId = 1L;

        // repository에서 delete() 가 호출될 것
        BDDMockito.willDoNothing().given(articleCommentRepository).delete(ArgumentMatchers.any(ArticleComment.class));

        // when
        sut.deleteArticleComment(articleCommentId);

        // then
        // repository에서 delete() 가 호출되었는지 확인
        BDDMockito.then(articleCommentRepository).should().delete(ArgumentMatchers.any(ArticleComment.class));
    }

}