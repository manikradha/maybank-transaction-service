package com.maybank.transaction.utils;

import java.util.Arrays;

public enum SortDirection {
    ASC, DESC;

    public static SortDirection fromString(String value) {
        try {
            return SortDirection.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Invalid sort direction. Allowed values are: " +
                            Arrays.toString(values())
            );
        }
    }
}