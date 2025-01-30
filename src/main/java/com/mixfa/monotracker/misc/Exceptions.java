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

}
