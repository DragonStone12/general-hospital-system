package com.pam.dispatcherservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class ApplicationFunctionsTests {
    @Autowired
    private InputDestination input;

    @Autowired
    private ApplicationFunctions applicationFunctions;

    @Test
    void shouldHandleAppointmentCreatedEvent() {
        // Given
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
            1L,
            "William B. Yeats", "" +
            "Dr. Strangelove",
            LocalDateTime.now());

        Message<AppointmentCreatedEvent> message = MessageBuilder.withPayload(event)
            .build();

        // When
        input.send(message);

        // Then
        assertThat(applicationFunctions.getReceivedEvents())
            .hasSize(1)
            .contains(event);
    }

    @Test
    void handleAppointmentShouldAddEventToReceivedEvents() {
        // Given
        Consumer<Message<AppointmentCreatedEvent>> handler = applicationFunctions.handleAppointment();

        LocalDateTime appointmentTime = LocalDateTime.now();
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
            1L,
            "John Doe",
            "Dr. Smith",
            appointmentTime
        );
        Message<AppointmentCreatedEvent> message = MessageBuilder.withPayload(event).build();

        // When
        handler.accept(message);

        // Then
        assertThat(applicationFunctions.getReceivedEvents())
            .hasSize(1)
            .first()
            .isEqualTo(event);
    }
}
