package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),   // @MappedSuperclass 로 분리했지만 인덱싱은 여기서 헤야 함
        @Index(columnList = "createdBy")
}) // db의 인덱싱 등록
@Entity
public class Article extends AuditingFields {
    // Auditing을 분리하는 방법 2가지
    // @Embedded: 공통 부분을 클래스로 만들어서 그걸 사용하는 곳에 필드로 추가하여 사용
    // @MappedSuperclass: 공통 부분을 클래스로 만들어서 그걸 사용하는 곳에서 상속받아 사용

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false, length = 10000)
    private String content;

    @Setter
    private String hashtag;

    @ToString.Exclude   // 순환 참조 끊기
    @OrderBy("id")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)    // ArticleComment 객체의 @ManyToOne 가 붙은 필드명을 넣어주기
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    // JPA 엔티티 하이버네이트는 기본 생성자 필수
    protected Article() {}

    private Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    // 펙토리 메서드로 생성자 생성하게 만들기
    public static Article of(String title, String content, String hashtag) {
        return new Article(title, content, hashtag);
    }

    // 게시글을 나중에 리스트나 컬렉션에서 사용시 편의 기능 추가
    // @EqualsAndHashCode 애노테이션은 모든 필드에 대한 걸 기준으로 만드므로 필드를 선택하려면 코드로 작성 필요\
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
