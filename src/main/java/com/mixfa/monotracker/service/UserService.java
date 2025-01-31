package com.mixfa.monotracker.service;

import com.mixfa.monotracker.misc.AppException;
import com.mixfa.monotracker.model.User;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    User register(User.RegisterRequest request) throws Exception;

    User update(String userId, User.UpdateRequest userUpdateRequest) throws AppException;

    Page<User> listUsers(Pageable pageable);

    Optional<User> findByMonoAccount(String accountId);

    @Getter
    @Accessors(fluent = true)
    class UserRegisterEvent extends ApplicationEvent {
        private final User user;

        public UserRegisterEvent(User user, Object source) {
            super(source);
            this.user = user;
        }
    }
}
