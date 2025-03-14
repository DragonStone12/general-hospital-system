package com.pam.dispatcherservice;

import org.junit.jupiter.api.Test;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;


@SpringJUnitConfig
class DispatcherServiceApplicationTests {
    @Test
    void testEventCreationAndAccessors() {
        LocalDateTime time = LocalDateTime.now();
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(1L, "John Doe", "Dr. Smith", time);

        assertThat(event.id()).isEqualTo(1L);
        assertThat(event.patientName()).isEqualTo("John Doe");
        assertThat(event.doctorName()).isEqualTo("Dr. Smith");
        assertThat(event.appointmentTime()).isEqualTo(time);
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime time = LocalDateTime.now();
        AppointmentCreatedEvent event1 = new AppointmentCreatedEvent(1L, "John Doe", "Dr. Smith", time);
        AppointmentCreatedEvent event2 = new AppointmentCreatedEvent(1L, "John Doe", "Dr. Smith", time);
        AppointmentCreatedEvent differentEvent = new AppointmentCreatedEvent(2L, "Jane Doe", "Dr. Jones", time);

        assertThat(event1)
            .isEqualTo(event2)
            .hasSameHashCodeAs(event2)
            .isNotEqualTo(differentEvent);
    }

    @Test
    void shouldAddAndRetrieveEvents() {
        ApplicationFunctions applicationFunctions = new ApplicationFunctions();
        // Given
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
            1L, "John Doe", "Dr. Smith", LocalDateTime.now());
        Message<AppointmentCreatedEvent> message = MessageBuilder.withPayload(event).build();
        Consumer<Message<AppointmentCreatedEvent>> handler = applicationFunctions.handleAppointment();

        // When
        handler.accept(message);

        // Then
        List<AppointmentCreatedEvent> events = applicationFunctions.getReceivedEvents();
        assertThat(events)
            .hasSize(1)
            .containsExactly(event);
    }

    @Test
    void shouldReturnEmptyListWhenNoEvents() {
        ApplicationFunctions applicationFunctions = new ApplicationFunctions();
        assertThat(applicationFunctions.getReceivedEvents()).isEmpty();
    }

    @Test
    void testToString() {
        LocalDateTime time = LocalDateTime.now();
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(1L, "John Doe", "Dr. Smith", time);

        assertThat(event.toString())
            .contains("1")
            .contains("John Doe")
            .contains("Dr. Smith")
            .contains(time.toString());
    }

    @Test
    void contextLoads() {
        assertThatCode(() -> DispatcherServiceApplication.main(new String[]{}))
            .doesNotThrowAnyException();
    }

    @Test
    void constructorCreatesInstance() {
        // when
        DispatcherServiceApplication application = new DispatcherServiceApplication();

        // then
        assertThat(application).isNotNull();
    }
}

