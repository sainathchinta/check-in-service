package org.siemens.check_in_service.Service;


public interface EmployeeAttendanceService {

    /**
     * Processes employee check-in or check-out based on their current attendance status.
     */
    boolean processCheckInAndCheckOut(String employeeId);
}
