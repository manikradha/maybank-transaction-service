package com.maybank.transaction.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum SortField {
    TRX_DATE(TransactionFields.TRX_DATE),
    TRX_AMOUNT(TransactionFields.TRX_AMOUNT),
    DESCRIPTION(TransactionFields.DESCRIPTION),
    CUSTOMER_ID(TransactionFields.CUSTOMER_ID);

    private final String fieldName;

    SortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static SortField fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Sort field cannot be null");
        }

        try {
            return SortField.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Arrays.stream(values())
                    .filter(v -> v.fieldName.equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid sort field '" + value + "'. Allowed values are: " +
                                    Arrays.stream(values())
                                            .map(Enum::name)
                                            .collect(Collectors.joining(", ")) +
                                    " or their field names: " +
                                    Arrays.stream(values())
                                            .map(SortField::getFieldName)
                                            .collect(Collectors.joining(", "))
                    ));
        }
    }
}
