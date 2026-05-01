package com.autoservice.domain.customer;

import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class CustomerValidator extends Validator {

    private final Customer customer;

    public CustomerValidator(final Customer aCustomer, final ValidationHandler aHandler) {
        super(aHandler);
        this.customer = aCustomer;
    }

    @Override
    public void validate() {
        checkConstraints();
        if (this.customer.getPhoneNumber() != null) {
            new PhoneNumberValidator(this.customer.getPhoneNumber(), this.validationHandler()).validate();
        }
        if (this.customer.getAddress() != null) {
            new AddressValidator(this.customer.getAddress(), this.validationHandler()).validate();
        }
        if (this.customer.getDocument() != null) {
            new DocumentValidator(this.customer.getDocument(), this.validationHandler()).validate();
        }
        if (this.customer.getRegistrationDate() != null) {
            new RegistrationDateValidator(this.customer.getRegistrationDate(), this.validationHandler()).validate();
        }
    }

    private void checkConstraints() {
        final var name = this.customer.getName();
        if (name == null || name.isBlank()) {
            this.validationHandler().append(new Error("Name should not be null or empty"));
            return;
        }

        int length = name.trim().length();
        if (length > 255 || length < 3) {
            this.validationHandler().append(new Error("Name should be between 3 and 255 characters"));
        }
    }
}
