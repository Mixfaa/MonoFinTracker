package com.mixfa.monotracker.controller;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.model.User;
import com.mixfa.monotracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User.RegisterRequest request) throws Exception {
        return userService.register(request);
    }

    @PatchMapping("/{id}/update")
    public User update(@PathVariable String id, User.UpdateRequest request) throws AppException {
        return userService.update(id, request);
    }
}
