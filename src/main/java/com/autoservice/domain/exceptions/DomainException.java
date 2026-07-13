package com.autoservice.domain.exceptions;

import com.autoservice.validation.Error;

import java.util.List;

public class DomainException extends NoStackTraceException {

    private final List<Error> errors;

    private DomainException(final String aMessage, final List<Error> anErrors) {
        super(aMessage);
        this.errors = anErrors;
    }

    public static DomainException with(final Error anErrors) {
        return new DomainException(anErrors.message(), List.of(anErrors));
    }

    public static DomainException with(final List<Error> anErrors) {
        final var message = anErrors.stream()
                .map(Error::message)
                .reduce((current, next) -> current + "; " + next)
                .orElse("");

        return new DomainException(message, anErrors);
    }

    public List<Error> getErrors() {
        return errors;
    }
}
