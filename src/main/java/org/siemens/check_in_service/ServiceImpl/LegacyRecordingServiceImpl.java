package org.siemens.check_in_service.ServiceImpl;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.siemens.check_in_service.KafkaService.KafkaPayload.CheckOutEventPayload;
import org.siemens.check_in_service.Service.LegacyRecordingService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LegacyRecordingServiceImpl implements LegacyRecordingService {

    @RateLimiter(name = "legacySystemLimiter")
    @Override
    public void recordHours(CheckOutEventPayload payload) {
        // This method is triggered after the checkout event is processed and received by the labor recording system.
        // Call underlying labor legacy methods
    }
}
