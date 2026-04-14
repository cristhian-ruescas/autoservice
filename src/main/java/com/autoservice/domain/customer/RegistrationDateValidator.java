package com.autoservice.domain.customer;

import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

import java.time.LocalDate;

public class RegistrationDateValidator extends Validator {

    private final RegistrationDate registrationDate;

    public RegistrationDateValidator(final RegistrationDate aRegistrationDate, final ValidationHandler aHandler) {
        super(aHandler);
        this.registrationDate = aRegistrationDate;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final var value = this.registrationDate.getValue();
        if (value == null) {
            this.validationHandler().append(new Error("Registration date should not be null"));
            return;
        }

        if (value.isAfter(LocalDate.now())) {
            this.validationHandler().append(new Error("Registration date cannot be in the future"));
        }
    }
}
