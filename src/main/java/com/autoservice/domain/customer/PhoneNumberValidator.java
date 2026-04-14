package com.autoservice.domain.customer;

import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class PhoneNumberValidator extends Validator {

    private final PhoneNumber phoneNumber;

    public PhoneNumberValidator(final PhoneNumber aPhoneNumber, final ValidationHandler aHandler) {
        super(aHandler);
        this.phoneNumber = aPhoneNumber;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final var value = this.phoneNumber.getValue();
        if (value == null || value.isBlank()) {
            this.validationHandler().append(new Error("Phone number should not be null or empty"));
            return;
        }

        // Simple regex for phone number, e.g., +1234567890 or 123-456-7890
        if (!value.matches("^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$")) {
            this.validationHandler().append(new Error("Phone number format is invalid"));
        }
    }
}
