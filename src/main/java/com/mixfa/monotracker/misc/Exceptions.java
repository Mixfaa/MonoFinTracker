package com.mixfa.monotracker.misc;

import lombok.experimental.UtilityClass;


@UtilityClass
public class Exceptions {
    public static AppException usernameTaken(String username) {
        return new AppException("Username " + username + " already taken", AppException.Type.USERNAME_TAKEN);
    }

    public static AppException cantConvertCurrency(int codeA, int codeB) {
        return new AppException("Can`t convert currency " + codeA + " to " + codeB, AppException.Type.CANT_CONVERT_CURRENCY);
    }

    public static AppException userNotFound(String userId) {
        return new AppException("User with id " + userId + " not found", AppException.Type.USER_NOT_FOUND);
    }

    public static AppException internalServerError(Throwable error) {
        if (error != null)
            error.printStackTrace(System.err);
        return new AppException("Internal server error", AppException.Type.INTERNAL_SERVER_ERROR);
    }

    public static AppException accessDenied() {
        return new AppException("Access denied", AppException.Type.ACCESS_DENIED);
    }
}
