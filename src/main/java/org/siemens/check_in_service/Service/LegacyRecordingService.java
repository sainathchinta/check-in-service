package org.siemens.check_in_service.Service;

import org.siemens.check_in_service.KafkaService.KafkaPayload.CheckOutEventPayload;
import org.springframework.stereotype.Service;

@Service
public interface LegacyRecordingService {

    /**
     * Records the employee's worked hours in the legacy recording system.
     */
    void recordHours(CheckOutEventPayload payload);
}
