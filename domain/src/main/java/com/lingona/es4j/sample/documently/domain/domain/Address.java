package com.lingona.es4j.sample.documently.domain.domain;

public class Address {

    private final String street;
    private final String streetNumber;
    private final String postalCode;
    private final String city;

    public Address(String street, String number, String code, String city) {
        this.street = street;
        this.streetNumber = number;
        this.postalCode = code;
        this.city = city;
    }
}