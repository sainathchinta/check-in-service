package org.siemens.check_in_service.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.siemens.check_in_service.KafkaService.KafkaPayload.CheckOutEventPayload;
import org.siemens.check_in_service.Service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendHoursReport(CheckOutEventPayload payload) {
        try {
            // NOTE: Currently this only logs the email sending for demonstration purposes.
            // To send real emails, configure SMTP properties in application.properties (host, port, username, password)

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(payload.getEmailId());
            message.setSubject("Your Work Hours Report");
            message.setText("Hello, \n\nYou worked " + payload.getHoursWorked() + " hours today.\n\nRegards,\nCheckOut Team");
            mailSender.send(message);

            log.info("Email sent to {} for {} hours", payload.getEmailId(), payload.getHoursWorked());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", payload.getEmailId(), e.getMessage());
        }
    }
}
