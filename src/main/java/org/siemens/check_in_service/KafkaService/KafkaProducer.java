package org.siemens.check_in_service.KafkaService;

import lombok.RequiredArgsConstructor;
import org.siemens.check_in_service.Entity.EmployeeAttendance;
import org.siemens.check_in_service.KafkaService.KafkaPayload.CheckOutEventPayload;
import org.siemens.check_in_service.Service.EmailService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, CheckOutEventPayload> kafkaTemplate;
    private final EmailService emailService;

    /**
     * sends emails to employees
     * Publishes checkout event to legacy recording system to log the employee's working hours
     *
     * @param attendance
     */
    @Retryable(
            value = {Exception.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishCheckOutEvent(EmployeeAttendance attendance) {
        CheckOutEventPayload payload = getCheckOutEventPayload(attendance);
        emailService.sendHoursReport(payload);
        kafkaTemplate.send(KafkaTopics.CHECK_OUT_EVENTS, payload);
    }

    @Recover
    public void recover(Exception e, EmployeeAttendance attendance) {
        publishToDLQ(getCheckOutEventPayload(attendance));
        System.err.println("Failed to publish checkout event for employee " + attendance.getEmployeeId());
    }

    private static CheckOutEventPayload getCheckOutEventPayload(EmployeeAttendance attendance) {
        CheckOutEventPayload payload = CheckOutEventPayload.builder()
                .employeeId(attendance.getEmployeeId())
                .hoursWorked(Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toHours())
                .emailId(attendance.getEmailId())
                .version(1)
                .build();
        return payload;
    }

    /**
     * Events that fail to be processed by the legacy system after retries are sent to DLQ
     *
     * @param checkOutEventPayload
     */
    public void publishToDLQ(CheckOutEventPayload checkOutEventPayload) {
        kafkaTemplate.send(KafkaTopics.CHECK_OUT_EVENTS_DLQ, checkOutEventPayload);
    }
}
