package com.mixfa.monotracker.misc;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@UtilityClass
public class MccCodeTable {
    private static Predicate<Integer> rangePredicate(int from, int to) {
        return i -> i >= from && i <= to;
    }

    private final static Map<Predicate<Integer>, String> rangeToTemplateCode = new HashMap<>(){
        {
            put(rangePredicate(1, 1499), "Agricultural services");
            put(rangePredicate(1500, 2999), "Contracted services");
            put(rangePredicate(4000, 4799), "Transportation services");
            put(rangePredicate(4800, 4999), "Utility services");
            put(rangePredicate(5000, 5599), "Retail outlet services");
            put(rangePredicate(5600, 5699), "Clothing shops");
            put(rangePredicate(4800, 4999), "Utilities");
            put(rangePredicate(5700, 7299), "Miscellaneous shops");
            put(rangePredicate(7300, 7999), "Business services");
            put(rangePredicate(8000, 8999), "Professional services and membership organizations");
            put(rangePredicate(9000, 9999), "Government services");
        }
    };

    public String getDescription(int code) {
        for (Map.Entry<Predicate<Integer>, String> entry : rangeToTemplateCode.entrySet())
            if (entry.getKey().test(code))
                return entry.getValue();

        throw new IllegalArgumentException("Invalid mcc code");
    }
}
