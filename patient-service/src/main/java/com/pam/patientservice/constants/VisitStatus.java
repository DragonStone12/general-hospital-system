package com.pam.patientservice.constants;

public enum VisitStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    NO_SHOW;

    public static VisitStatus fromStringValue(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unrecognized type " + type);
        }
    }
}
