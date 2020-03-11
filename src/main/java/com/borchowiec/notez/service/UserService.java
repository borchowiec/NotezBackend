package com.borchowiec.notez.service;

import com.borchowiec.notez.model.User;
import com.borchowiec.notez.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserOfUsernameOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user of username: " + username));
    }
}
