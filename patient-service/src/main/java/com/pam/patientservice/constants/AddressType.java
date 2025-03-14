package com.pam.patientservice.constants;

public enum AddressType {
    MAIL,
    HOME,
    WORK;

    public static AddressType fromStringValue(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unrecognized type" + type);
        }
    }
}
