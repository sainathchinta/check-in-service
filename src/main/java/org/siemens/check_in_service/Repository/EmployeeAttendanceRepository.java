package org.siemens.check_in_service.Repository;

import org.siemens.check_in_service.Entity.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, Long> {
    Optional<EmployeeAttendance> findTopByEmployeeIdOrderByCheckInTimeDesc(String employeeId);
}

