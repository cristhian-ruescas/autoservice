package com.autoservice.application;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationOutputTest {

    @Test
    void fromCalculaTotalPages() {
        final var p = PaginationOutput.from(List.of("a", "b"), 0, 10, 25L);
        assertEquals(3, p.totalPages());
        assertEquals(25L, p.totalElements());
        assertEquals(10, p.size());
    }

    @Test
    void sizeZeroTotalPagesZero() {
        final var p = PaginationOutput.from(List.of(), 0, 0, 100L);
        assertEquals(0, p.totalPages());
    }
}
