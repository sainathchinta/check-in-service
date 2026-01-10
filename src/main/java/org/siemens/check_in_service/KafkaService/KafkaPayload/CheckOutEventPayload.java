package org.siemens.check_in_service.KafkaService.KafkaPayload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutEventPayload {
    private String employeeId;
    private long hoursWorked;
    private String emailId;
    private int version; // future purpose
}
