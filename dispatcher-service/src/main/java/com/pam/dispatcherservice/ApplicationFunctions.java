package com.pam.dispatcherservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class ApplicationFunctions {

    @Bean
    public Consumer<Message<AppointmentCreatedEvent>> handleAppointment() {
        return message -> log.info("Appointment created event received: {}", message.getPayload());

    }
}
