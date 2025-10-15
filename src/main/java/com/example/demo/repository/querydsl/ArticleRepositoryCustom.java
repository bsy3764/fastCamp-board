package com.example.demo.repository.querydsl;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<String> findALlDistinctHashtags();
}
