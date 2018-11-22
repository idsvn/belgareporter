package be.belga.reporter.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.belga.reporter.config.AppConfiguration;

@Service
public class EmailSenderService {
    private static Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

    private final AppConfiguration appConfiguration;

    @Autowired
    public EmailSenderService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public void sendEmail(String toAddr, String subject, String content) {

        logger.info("Parse and Send Email");
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", appConfiguration.MAIL_SMTP_AUTH);
            props.put("mail.smtp.starttls.enable", appConfiguration.MAIL_SMTP_STARTTLS);
            props.put("mail.smtp.host", appConfiguration.MAIL_SMTP_HOST);
            props.put("mail.smtp.port", appConfiguration.MAIL_SMTP_PORT);

            final String username = appConfiguration.EMAIL_USERNAME;
            final String password = appConfiguration.EMAIL_PASSWORD;

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddr));

            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

        } catch (MessagingException ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.info("Send Email DONE");
    }
}
