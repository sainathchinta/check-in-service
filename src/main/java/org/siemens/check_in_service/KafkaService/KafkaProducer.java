package org.siemens.check_in_service.KafkaService;

import lombok.RequiredArgsConstructor;
import org.siemens.check_in_service.Entity.EmployeeAttendance;
import org.siemens.check_in_service.KafkaService.KafkaPayload.CheckOutEventPayload;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, CheckOutEventPayload> kafkaTemplate;

    /**
     * Publishes checkout event to legacy recording system to log the employee's working hours
     *
     * @param attendance
     */
    public void publishCheckOutEvent(EmployeeAttendance attendance) {
        CheckOutEventPayload payload = CheckOutEventPayload.builder()
                .employeeId(attendance.getEmployeeId())
                .hoursWorked(Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toHours())
                .emailId(attendance.getEmailId())
                .version(1)
                .build();

        kafkaTemplate.send(KafkaTopics.CHECK_OUT_EVENTS, payload);
    }

    /**
     * Events that fail to be processed by the legacy system after retries are sent to DLQ
     *
     * @param checkOutEventPayload
     */
    public void publishToDLQ(CheckOutEventPayload checkOutEventPayload){
        kafkaTemplate.send(KafkaTopics.CHECK_OUT_EVENTS_DLQ, checkOutEventPayload);
    }
}
