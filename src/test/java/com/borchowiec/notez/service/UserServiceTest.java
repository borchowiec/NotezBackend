package com.borchowiec.notez.service;

import com.borchowiec.notez.model.User;
import com.borchowiec.notez.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    private void init() {
        userService = new UserService(userRepository);
    }

    @Test
    void getUserOfUsernameOrThrowException_userExists_shouldReturnUserOfGivenUsername() {
        // given
        String username = "username";

        // when
        User expected = new User(username, "email", "password");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(expected));
        User result = userService.getUserOfUsernameOrThrowException(username);

        // then
        assertEquals(expected, result);
    }

    @Test
    void getUserOfUsernameOrThrowException_userDoesntExist_shouldThrowException() {
        // given
        String username = "username";

        // when
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // then
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserOfUsernameOrThrowException(username));
    }
}