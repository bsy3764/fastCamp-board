package com.example.demo.repository;

import com.example.demo.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource // 도메인 모델과 repository를 분석해서, RESTful API를 제공 -> 실헹해서 http://localhost:8080/api 확인해보기(spring-data-rest-hal-explorer)
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
