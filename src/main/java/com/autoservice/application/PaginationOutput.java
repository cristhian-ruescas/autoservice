package com.autoservice.application;

import java.util.List;

public record PaginationOutput<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static <T> PaginationOutput<T> from(
            final List<T> items,
            final int page,
            final int size,
            final long totalElements
    ) {
        final int totalPages = size == 0
                ? 0
                : (int) Math.ceil((double) totalElements / size);

        return new PaginationOutput<>(
                items,
                page,
                size,
                totalElements,
                totalPages
        );
    }
}
