package com.example.demo.repository;

import com.example.demo.domain.Article;
import com.example.demo.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource // 도메인 모델과 repository를 분석해서, RESTful API를 제공 -> 실헹해서 http://localhost:8080/api 확인해보기(spring-data-rest-hal-explorer)
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>, // 엔티티 안에 있는 모든 필드에 대한 기본 검색기능을 추가
        QuerydslBinderCustomizer<QArticle>  // 다양한 sql 조건문을 생성하기 위한 커스터마이징
{
    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy);
//        bindings.bind(root.title).first(((path, value) -> path.eq(value)));
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // containsIgnoreCase -> like '%~~%'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
