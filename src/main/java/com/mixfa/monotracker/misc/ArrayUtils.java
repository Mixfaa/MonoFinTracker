package com.mixfa.monotracker.misc;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Function;

@UtilityClass
public class ArrayUtils {
    public static <T> T[] add(T[] array, Function<Integer, T[]> constructor, T element) {
        var newArray = constructor.apply(array.length + 1);
        System.arraycopy(array, 0, newArray, 0, array.length - 1);
        newArray[array.length] = element;

        return newArray;
    }

    public static <T> T[] remove(T[] array, Function<Integer, T[]> constructor, T element) {
        int indexToRemove = -1;

        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], element)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove < 0) return array;

        var newArray = constructor.apply(array.length - 1);

        System.arraycopy(array, 0, newArray, 0, indexToRemove);
        System.arraycopy(array, indexToRemove + 1, newArray, indexToRemove, array.length - indexToRemove - 1);

        return newArray;
    }
}
