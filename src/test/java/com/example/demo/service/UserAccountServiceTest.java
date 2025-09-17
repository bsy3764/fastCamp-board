package com.example.demo.service;

import com.example.demo.domain.UserAccount;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("유저 테스트")
@ExtendWith(MockitoExtension.class) // mockito 사용
class UserAccountServiceTest {

    @InjectMocks // mock을 주입하는 객체
    private UserAccountService sut;

    @Mock   // @InjectMocks 이외의 객체들에 붙임
    private UserAccountRepository repository;

    @DisplayName("회원Id로 회원 데이터 반환")
    @Test
    void searchUserByUserId() {
        // given
        String userId = "uno";
        BDDMockito.given(repository.findById(userId)).
                willReturn(Optional.of(UserAccount.of(userId, "password", "test@mail.com", "nick")));

        // when
        Optional<UserAccountDto> user = sut.searchUser(userId);

        // then
        assertThat(user).isNotNull();
        BDDMockito.then(repository).should().findById(userId);
    }

    @DisplayName("없는 회원Id로 검색하면 빈 Optional 반환")
    @Test
    void searchUserByNoExistUserId() {
        // given
        String userId = "empty";
        BDDMockito.given(repository.findById(userId)).
                willReturn(Optional.empty());

        // when
        Optional<UserAccountDto> user = sut.searchUser(userId);

        // then
        assertThat(user).isEmpty();
        BDDMockito.then(repository).should().findById(userId);
    }

    @DisplayName("회원가입")
    @Test
    void createUser() {
        // given
        UserAccount userAccount = UserAccount.of("uno", "password", "e@mail.com", "nick");
        UserAccount saved = UserAccount.of("uno", "password", "e@mail.com", "nick");

        BDDMockito.given(repository.save(userAccount)).
                willReturn(saved);

        // when
        UserAccountDto user = sut.saveUser(userAccount);

        // then
        assertThat(user).isNotNull();
        BDDMockito.then(repository).should().save(userAccount);
    }

}