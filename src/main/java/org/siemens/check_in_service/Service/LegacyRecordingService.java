package org.siemens.check_in_service.Service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.siemens.check_in_service.KafkaService.KafkaPayload.CheckOutEventPayload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LegacyRecordingService {

    @RateLimiter(name = "legacySystemLimiter")
    public void recordHours(CheckOutEventPayload payload) {
        // Call the legacy system
    }
}
