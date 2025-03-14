package com.pam.patientservice.constants;

public enum VisitType {
    NEW,
    FOLLOW_UP,
    PROCEDURE;

    public static VisitType fromStringValue(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unrecognized type " + type);
        }
    }

}
