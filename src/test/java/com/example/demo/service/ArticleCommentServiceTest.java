package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.ArticleComment;
import com.example.demo.domain.UserAccount;
import com.example.demo.dto.*;
import com.example.demo.repository.ArticleCommentRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class) // mockito 사용
class ArticleCommentServiceTest {

    @InjectMocks
    private ArticleCommentService sut;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCommentRepository articleCommentRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트를 반환")
    @Test
    void getCommentsByArticleId() {
        // given
        Long articleId = 1L;
        ArticleComment comment = createArticleComment("content");
        BDDMockito.given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(
                List.of(comment));

        // when
        List<ArticleCommentDto> comments = sut.searchArticleComments(articleId);

        // then
        assertThat(comments).isNotNull();
        BDDMockito.then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 저장")
    @Test
    void createComment() {
        // given
        ArticleCommentDto dto = createArticleCommentDto("content");
        BDDMockito.given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        BDDMockito.given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        // repository에서 save() 가 호출될 것
        BDDMockito.given(articleCommentRepository.save(ArgumentMatchers.any(ArticleComment.class)))
                .willReturn(null);

        // when
        sut.saveArticleComment(dto);

        // then
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(articleRepository).should().getReferenceById(dto.articleId());
        BDDMockito.then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        BDDMockito.then(articleCommentRepository).should().save(ArgumentMatchers.any(ArticleComment.class));
    }

    @DisplayName("댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 안 함")
    @Test
    void saveCommentNotExistArticle() {
        // given
        ArticleCommentDto dto = createArticleCommentDto("content");
        BDDMockito.given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

        // when
        sut.saveArticleComment(dto);

        // then
        // repository에서 save() 가 호출되었는지 확인
        BDDMockito.then(articleRepository).should().getReferenceById(dto.articleId());
        BDDMockito.then(userAccountRepository).shouldHaveNoInteractions();
        BDDMockito.then(articleCommentRepository).should().save(ArgumentMatchers.any(ArticleComment.class));
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 수정")
    @Test
    void updateComment() {
        // given
        String oldContent = "content";
        String updatedContent = "댓글";
        ArticleComment articleComment = createArticleComment(oldContent);
        ArticleCommentDto dto = createArticleCommentDto(updatedContent);
        BDDMockito.given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);

        // repository에서 getReferenceById() 가 호출될 것
        BDDMockito.given(articleCommentRepository.getReferenceById(dto.id()))
                .willReturn(articleComment);

        // when
        sut.updateArticleComment(dto);

        // then
        assertThat(articleComment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(updatedContent);
        // repository에서 getReferenceById() 가 호출되었는지 확인
        BDDMockito.then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 댓글 정보를 수정하려고 하면, 경고 로그를 찍고 아무 것도 안 함")
    @Test
    void updateArticleComment_nonExistArticleComment() {
        // Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        BDDMockito.given(articleCommentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticleComment(dto);

        // Then
        BDDMockito.then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제")
    @Test
    void deleteComment() {
        // given
        Long articleCommentId = 1L;

        // repository에서 delete() 가 호출될 것
        BDDMockito.willDoNothing().given(articleCommentRepository).deleteById(articleCommentId);

        // when
        sut.deleteArticleComment(articleCommentId);

        // then
        // repository에서 delete() 가 호출되었는지 확인
        BDDMockito.then(articleCommentRepository).should().deleteById(articleCommentId);
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
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

}