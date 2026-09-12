package com.autoservice.infrastructure.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestCorrelationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RequestCorrelationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new RequestCorrelationFilter();
        MDC.clear();
    }

    @Test
    void doFilterInternal_usaHeaderDeCorrelacaoExistente() throws ServletException, IOException {
        when(request.getHeader(RequestCorrelationFilter.CORRELATION_ID_HEADER)).thenReturn("corr-123");
        doAnswer(invocation -> {
            assertEquals("corr-123", MDC.get(RequestCorrelationFilter.CORRELATION_ID_MDC_KEY));
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(RequestCorrelationFilter.CORRELATION_ID_HEADER, "corr-123");
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get(RequestCorrelationFilter.CORRELATION_ID_MDC_KEY));
    }
}
