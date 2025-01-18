package com.pam.dispatcherservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class ApplicationFunctions {

    private final List<AppointmentCreatedEvent> receivedEvents = new CopyOnWriteArrayList<>();

    @Bean
    public Consumer<Message<AppointmentCreatedEvent>> handleAppointment() {
        return message -> receivedEvents.add(message.getPayload());
    }

    public List<AppointmentCreatedEvent> getReceivedEvents() {
        return new ArrayList<>(receivedEvents);
    }
}

