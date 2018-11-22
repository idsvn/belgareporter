package be.belga.reporter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfiguration {

    @Value("${mail.smtp.auth}")
    public String MAIL_SMTP_AUTH;

    @Value("${mail.smtp.starttls.enable}")
    public String MAIL_SMTP_STARTTLS;

    @Value("${mail.smtp.host}")
    public String MAIL_SMTP_HOST;

    @Value("${mail.smtp.port}")
    public String MAIL_SMTP_PORT;

    @Value("${mail.username}")
    public String EMAIL_USERNAME;

    @Value("${mail.password}")
    public String EMAIL_PASSWORD;

}
