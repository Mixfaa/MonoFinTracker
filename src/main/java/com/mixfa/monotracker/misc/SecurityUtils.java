package com.mixfa.monotracker.misc;

import com.mixfa.monotracker.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {
    private static Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void assertAuthenticated(User user) throws AppException {
        var auth = getAuth();
        if (!auth.isAuthenticated() || auth.getName().equals(user.getUsername()))
            throw Exceptions.accessDenied();
    }
}
