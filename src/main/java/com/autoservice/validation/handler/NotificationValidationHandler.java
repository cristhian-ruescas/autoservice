package com.autoservice.validation.handler;

import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationValidationHandler implements ValidationHandler {

    private final List<Error> errors;

    public NotificationValidationHandler() {
        this.errors = new ArrayList<>();
    }

    @Override
    public ValidationHandler append(final Error anError) {
        this.errors.add(anError);
        return this;
    }

    @Override
    public ValidationHandler append(final ValidationHandler handler) {
        this.errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(final Validation<T> validation) {
        try {
            return validation.validate();
        } catch (Exception ex) {
            this.errors.add(new Error(ex.getMessage()));
            return null;
        }
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }

    public boolean hasError() {
        return !this.errors.isEmpty();
    }
}