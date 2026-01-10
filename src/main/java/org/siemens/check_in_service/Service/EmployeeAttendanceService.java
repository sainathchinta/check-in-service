package org.siemens.check_in_service.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.siemens.check_in_service.Entity.EmployeeAttendance;
import org.siemens.check_in_service.KafkaService.KafkaProducer;
import org.siemens.check_in_service.Repository.EmployeeAttendanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmployeeAttendanceService {

    private final EmployeeAttendanceRepository employeeAttendanceRepository;
    private final KafkaProducer kafkaProducer;

    @Transactional(rollbackOn = Exception.class)
    public boolean processCheckInAndCheckOut(String employeeId) {
        EmployeeAttendance lastRecord = employeeAttendanceRepository
                .findTopByEmployeeIdOrderByCheckInTimeDesc(employeeId)
                .orElse(null);

        if (Objects.isNull(lastRecord) || Objects.nonNull(lastRecord.getCheckOutTime())) {
            // New check-in
            EmployeeAttendance attendance = EmployeeAttendance.builder()
                    .employeeId(employeeId)
                    .emailId(employeeId + "gmail.com")
                    .checkInTime(LocalDateTime.now())
                    .reported(false)
                    .build();
            employeeAttendanceRepository.save(attendance);
            return true; // Checked in
        } else {
            // Check-out
            lastRecord.setCheckOutTime(LocalDateTime.now());
            employeeAttendanceRepository.save(lastRecord);

            // Publish event asynchronously to Kafka
            kafkaProducer.publishCheckOutEvent(lastRecord);
            return false; // Checked out
        }
    }
}
