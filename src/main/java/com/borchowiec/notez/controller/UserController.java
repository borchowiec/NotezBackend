package com.borchowiec.notez.controller;

import com.borchowiec.notez.model.User;
import com.borchowiec.notez.payload.UserInfoResponse;
import com.borchowiec.notez.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/me")
    public UserInfoResponse getInfoAboutPrincipal(Principal principal) {
        User user = userService.getUserOfUsernameOrThrowException(principal.getName());
        return new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
