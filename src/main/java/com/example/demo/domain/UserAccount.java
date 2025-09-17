package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@Entity
public class UserAccount extends AuditingFields {

    @Id
    @Column(nullable = false, length = 50)
    private String id;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(length = 100)
    private String email;

    @Setter
    @Column(length = 100)
    private String nickname;

    @Setter
    private String memo;

    // JPA 엔티티 하이버네이트는 기본 생성자 필수
    protected UserAccount() {}

    private UserAccount(String id, String password, String email, String nickname, String memo) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
    }

    public static UserAccount of(String id, String password, String email, String nickname, String memo) {
        return new UserAccount(id, password, email, nickname, memo);
    }

    public static UserAccount of(String id, String password, String email, String nickname) {
        return UserAccount.of(id, password, email, nickname, null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
