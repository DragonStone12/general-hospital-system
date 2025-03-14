package com.pam.patientservice.constants;

public enum GenderType {
    MALE,
    FEMALE;

    public static GenderType fromStringValue(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unrecognized type " + type);
        }
    }
}
