package com.autoservice.domain.customer;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.validation.ValidationHandler;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer", schema = "customer")
public class Customer extends AggregateRoot<CustomerID> {

    @EmbeddedId
    private final CustomerID id;

    @Column(name = "name", nullable = false)
    private final String name;

    @Embedded
    private final PhoneNumber phoneNumber;

    @Embedded
    private final Address address;

    @Embedded
    private final Document document;

    @Embedded
    private final RegistrationDate registrationDate;

    protected Customer() {
        this.id = null;
        this.name = null;
        this.phoneNumber = null;
        this.address = null;
        this.document = null;
        this.registrationDate = null;
    }

    private Customer(CustomerID aID, String aName, PhoneNumber aPhoneNumber, Address anAddress, Document aDocument, RegistrationDate aRegistrationDate) {
        super(aID);
        this.id = aID;
        this.name = aName;
        this.phoneNumber = aPhoneNumber;
        this.address = anAddress;
        this.document = aDocument;
        this.registrationDate = aRegistrationDate;
    }

    public static Customer newCustomer(String aName, PhoneNumber aPhoneNumber, Address anAddress, Document aDocument, RegistrationDate aRegistrationDate) {
        return new Customer(CustomerID.unique(), aName, aPhoneNumber, anAddress, aDocument, aRegistrationDate);
    }

    public static Customer withId(CustomerID id, String aName, PhoneNumber aPhoneNumber, Address anAddress, Document aDocument, RegistrationDate aRegistrationDate) {
        return new Customer(id, aName, aPhoneNumber, anAddress, aDocument, aRegistrationDate);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new CustomerValidator(this, handler).validate();
    }

    @Override
    public CustomerID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public Document getDocument() {
        return document;
    }

    public RegistrationDate getRegistrationDate() {
        return registrationDate;
    }
}
