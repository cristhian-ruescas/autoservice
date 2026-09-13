package com.autoservice.infrastructure.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class HttpAccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpAccessLogFilter.class);

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final var startedAt = System.currentTimeMillis();
        final var traceIds = DatadogTraceContext.current();
        try {
            filterChain.doFilter(request, response);
        } finally {
            final var durationMs = System.currentTimeMillis() - startedAt;
            try {
                DatadogTraceContext.applyToMdc(traceIds);
                log.info(
                        "event=http_access method={} path={} status={} duration_ms={} correlation_id={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        durationMs,
                        correlationId()
                );
            } finally {
                DatadogTraceContext.clearFromMdc();
            }
        }
    }

    private static String correlationId() {
        final var value = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY);
        return value == null ? "unknown" : value;
    }
}
