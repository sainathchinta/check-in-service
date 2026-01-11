package org.siemens.check_in_service.KafkaService;

import lombok.RequiredArgsConstructor;
import org.siemens.check_in_service.KafkaService.KafkaPayload.CheckOutEventPayload;
import org.siemens.check_in_service.Service.EmailService;
import org.siemens.check_in_service.Service.LegacyRecordingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LegacySystemCheckOutConsumer {

    private final LegacyRecordingService legacyRecordingService;
    private final KafkaProducer kafkaProducer;

    /**
     *  Listens to the CHECK_OUT_EVENTS topic. For each check-out event:
     *   1. Calls the legacy recording system to log hours worked (with a rate limiter to avoid overwhelming the legacy API).
     *
     * @param payload
     */
    @KafkaListener(topics = KafkaTopics.CHECK_OUT_EVENTS, groupId = "check-out-group")
    @Retryable(
            value = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void handleCheckOutEvent(CheckOutEventPayload payload) {
        // Call the legacy recording system to record hours worked.
        // A rate limiter is applied inside the LegacyRecordingService to prevent overloading the legacy API.
        // Currently configured to allow a maximum of 5 calls per defined refresh period, configurable via application.properties.
        legacyRecordingService.recordHours(payload);
    }

    @Recover
    public void recover(RuntimeException e, CheckOutEventPayload payload) {
        kafkaProducer.publishToDLQ(payload);
        System.err.println("Failed after retries, sending to DLQ: " + payload);
    }

}

