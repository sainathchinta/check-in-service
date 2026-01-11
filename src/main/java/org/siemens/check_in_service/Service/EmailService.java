package org.siemens.check_in_service.Service;

import org.siemens.check_in_service.KafkaService.KafkaPayload.CheckOutEventPayload;

public interface EmailService {
    /**
     * Sends a work hours report email to the employee.
     */
    void sendHoursReport(CheckOutEventPayload payload);
}
