package com.mixfa.monotracker.service.impl;

import com.mixfa.monotracker.misc.Exceptions;
import com.mixfa.monotracker.model.User;
import com.mixfa.monotracker.service.UserService;
import com.mixfa.monotracker.service.feign.MonoApi;
import com.mixfa.monotracker.service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final MonoApi monoApi;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public User register(User.RegisterRequest req) throws Exception {
        if (userRepo.existsByUsername(req.username()))
            throw Exceptions.usernameTaken(req.username());

        var clientInfo = monoApi.getClientInfo(req.xToken());

        var user = new User(
                req.username(),
                passwordEncoder.encode(req.password()),
                req.xToken(),
                clientInfo.clientId(),
                Arrays.stream(clientInfo.accounts())
                        .map(MonoApi.AccountInfo::id)
                        .toArray(String[]::new)
        );

        user = userRepo.save(user);

        eventPublisher.publishEvent(new UserService.UserRegisterEvent(user, this));

        return user;
    }

    @Override
    public Page<User> listUsers(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    @Override
    public Optional<User> findByMonoAccount(String accountId) {
        return userRepo.findByAccountsIdsContains(accountId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
