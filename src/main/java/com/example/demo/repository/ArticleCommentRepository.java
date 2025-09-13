package com.example.demo.repository;

import com.example.demo.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource // 도메인 모델과 repository를 분석해서, RESTful API를 제공
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
}
