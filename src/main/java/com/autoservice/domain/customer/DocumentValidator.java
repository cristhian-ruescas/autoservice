package com.autoservice.domain.customer;

import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.Validator;

public class DocumentValidator extends Validator {

    private final Document document;

    public DocumentValidator(final Document aDocument, final ValidationHandler aHandler) {
        super(aHandler);
        this.document = aDocument;
    }

    @Override
    public void validate() {
        checkConstraints();
    }

    private void checkConstraints() {
        final var value = this.document.getValue();
        if (value == null || value.isBlank()) {
            this.validationHandler().append(new Error("Document should not be null or empty"));
            return;
        }

        if (!value.matches("^[a-zA-Z0-9]{5,20}$")) {
            this.validationHandler().append(new Error("Document should be alphanumeric and between 5 and 20 characters"));
        }
    }
}
