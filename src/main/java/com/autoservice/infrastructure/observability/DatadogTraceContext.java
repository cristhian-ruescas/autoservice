package com.autoservice.infrastructure.observability;

import datadog.trace.api.CorrelationIdentifier;
import org.slf4j.MDC;

final class DatadogTraceContext {

    private DatadogTraceContext() {
    }

    static TraceIds current() {
        return new TraceIds(
                CorrelationIdentifier.getTraceId(),
                CorrelationIdentifier.getSpanId()
        );
    }

    static void applyToMdc(final TraceIds traceIds) {
        if (traceIds.hasTraceId()) {
            MDC.put(CorrelationIdentifier.getTraceIdKey(), traceIds.traceId());
        }
        if (traceIds.hasSpanId()) {
            MDC.put(CorrelationIdentifier.getSpanIdKey(), traceIds.spanId());
        }
    }

    static void clearFromMdc() {
        MDC.remove(CorrelationIdentifier.getTraceIdKey());
        MDC.remove(CorrelationIdentifier.getSpanIdKey());
    }

    record TraceIds(String traceId, String spanId) {
        boolean hasTraceId() {
            return traceId != null && !traceId.isBlank() && !"0".equals(traceId);
        }

        boolean hasSpanId() {
            return spanId != null && !spanId.isBlank() && !"0".equals(spanId);
        }
    }
}
