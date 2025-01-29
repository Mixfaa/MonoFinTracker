package com.mixfa.monotracker.misc;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Exceptions {

    public static AppException usernameTaken(String username) {
        return new AppException("Username " + username + " already taken", AppException.Type.USERNAME_TAKEN);
    }

}
