package be.belga.reporter.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

    @Autowired
    MessageSource message;

    public String getMessage(String key) {
        return message.getMessage(key, null, Locale.getDefault());
    }

}
