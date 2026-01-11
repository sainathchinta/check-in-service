package org.siemens.check_in_service.Controller;

import lombok.RequiredArgsConstructor;
import org.siemens.check_in_service.Service.EmployeeAttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/check-in-check-out")
@RequiredArgsConstructor
public class CheckInController {

    private final EmployeeAttendanceService employeeAttendanceService;

    @PostMapping("/{employeeId}")
    public ResponseEntity<String> checkInOut(@PathVariable String employeeId) {
        try {
            boolean checkedIn = employeeAttendanceService.processCheckInAndCheckOut(employeeId);
            return ResponseEntity.ok(checkedIn ? "Checked In" : "Checked Out");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong.Please Retry");
        }
    }
}
