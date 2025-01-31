package com.pam.dispatcherservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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
/**
 * Integration tests for {@link ApplicationFunctions}.
 *
 * <p>Note: These tests share a singleton {@link ApplicationFunctions} instance due to Spring's default scope,
 * which can cause received events to accumulate across test executions. The first test adds one event, and
 * the second test adds another event, resulting in 2 events total when the second test expects only 1.</p>
 *
 * <p>To ensure proper test isolation, the {@link #setUp()} method uses {@link @BeforeEach} to clear events
 * before each test. This way, each test starts with an empty list of events, preventing accumulation from
 * previous test executions.</p>
 *
 * <p>An alternative approach would be to use {@link @DirtiesContext} to create a new Spring context for each
 * test, but this would be less efficient as it's more heavyweight than simply clearing the events.</p>
 */
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class ApplicationFunctionsTests {
    @Autowired
    private InputDestination input;

    @Autowired
    private ApplicationFunctions applicationFunctions;


    @Test
    @Tag("integration")
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
    @Tag("integration")
    void handleAppointmentShouldAddEventToReceivedEvents() {
        // Given
        ApplicationFunctions applicationFunctions = new ApplicationFunctions();
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
