package com.autoservice.domain.customer;

import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class ZipCodeValidator extends Validator {

    private final ZipCode zipCode;

    public ZipCodeValidator(final ZipCode aZipCode, final ValidationHandler aHandler) {
        super(aHandler);
        this.zipCode = aZipCode;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final var value = this.zipCode.getValue();
        if (value == null || value.isBlank()) {
            this.validationHandler().append(new Error("Zip code should not be null or empty"));
            return;
        }

        // Brazilian CEP format: 00000-000
        if (!value.matches("^\\d{5}-\\d{3}$")) {
            this.validationHandler().append(new Error("Zip code format is invalid (expected: 00000-000)"));
        }
    }
}
