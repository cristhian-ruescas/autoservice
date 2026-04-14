package com.autoservice.domain.customer;

import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class AddressValidator extends Validator {

    private final Address address;

    public AddressValidator(final Address anAddress, final ValidationHandler aHandler) {
        super(aHandler);
        this.address = anAddress;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final var street = this.address.getStreet();
        if (street == null || street.isBlank()) {
            this.validationHandler().append(new Error("Street should not be null or empty"));
        }

        final var number = this.address.getNumber();
        if (number == null || number.isBlank()) {
            this.validationHandler().append(new Error("Number should not be null or empty"));
        }

        final var city = this.address.getCity();
        if (city == null || city.isBlank()) {
            this.validationHandler().append(new Error("City should not be null or empty"));
        }

        final var state = this.address.getState();
        if (state == null) {
            this.validationHandler().append(new Error("State should not be null"));
        }

        if (this.address.getZipCode() != null) {
            new ZipCodeValidator(this.address.getZipCode(), this.validationHandler()).validate();
        }
    }
}
