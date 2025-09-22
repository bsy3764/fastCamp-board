package com.example.demo.repository;

import com.example.demo.domain.ArticleComment;
import com.example.demo.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource // 도메인 모델과 repository를 분석해서, RESTful API를 제공
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>, // 엔티티 안에 있는 모든 필드에 대한 기본 검색기능을 추가
        QuerydslBinderCustomizer<QArticleComment>  // 다양한 sql 조건문을 생성하기 위한 커스터마이징
{
    List<ArticleComment> findByArticle_Id(Long articleId);

    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        bindings.excludeUnlistedProperties(true);
    }
}
