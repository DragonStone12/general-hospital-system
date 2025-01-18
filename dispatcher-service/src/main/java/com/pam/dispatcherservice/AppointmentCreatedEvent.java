package com.pam.dispatcherservice;

import java.time.LocalDateTime;

public record AppointmentCreatedEvent(
    Long id,
    String patientName,
    String doctorName,
    LocalDateTime appointmentTime
) {
}
