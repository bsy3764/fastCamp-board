package com.example.demo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.demo.domain.UserAccount}
 */
public record UserAccountDto(
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy,
        String id,
        String password,
        String email,
        String nickname,
        String memo
) {
    public static UserAccountDto of(LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, String id, String password, String email, String nickname, String memo) {
        return new UserAccountDto(createdAt, createdBy, modifiedAt, modifiedBy, id, password, email, nickname, memo);
    }

    public static UserAccountDto of(LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, String id, String password, String email, String nickname) {
        return new UserAccountDto(createdAt, createdBy, modifiedAt, modifiedBy, id, password, email, nickname, null);
    }
}