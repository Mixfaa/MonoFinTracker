package com.mixfa.monotracker.controller;

import com.mixfa.monotracker.model.User;
import com.mixfa.monotracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User.RegisterRequest request) throws Exception {
        return userService.register(request);
    }
}
