package com.autoservice.infrastructure.query;

public final class ValueObjectFormatter {

    private ValueObjectFormatter() {
    }

    public static String asString(final Object valueObject) {
        return valueObject == null ? null : valueObject.toString();
    }
}
