package com.example.demo.service;

import com.example.demo.domain.UserAccount;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserAccountService {

    private final UserAccountRepository repository;

    public Optional<UserAccountDto> searchUser(String userId) {
        return Optional.empty();
    }

    public UserAccountDto saveUser(UserAccount userAccount) {
        return UserAccountDto.of(LocalDateTime.now(), "uno", LocalDateTime.now(), "uno",
                "uno", "password", "e@mail.com", "nick");
    }
}
