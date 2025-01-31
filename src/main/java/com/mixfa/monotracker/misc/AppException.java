package com.mixfa.monotracker.misc;

import lombok.Getter;

@Getter
public class AppException extends Exception {
    private final Type type;

    public AppException(String msg, Type type) {
        super(msg);
        this.type = type;
    }

    public enum Type {
        USERNAME_TAKEN,
        CANT_CONVERT_CURRENCY,
        USER_NOT_FOUND,
        INTERNAL_SERVER_ERROR,
        ACCESS_DENIED
    }
}
