package com.example.demo.controller;

import com.example.demo.dto.UserAccountDto;
import com.example.demo.dto.request.ArticleCommentRequest;
import com.example.demo.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService service;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest) {
        service.saveArticleComment(articleCommentRequest.toDto(UserAccountDto.of(
                "uno","123123","e@mail.com","nick",null)));
        return "redirect:/articles" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId, Long articleId) {
        service.deleteArticleComment(commentId);
        return "redirect:/articles/" +  articleId;
    }
}
