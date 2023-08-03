package com.krish.automessaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.krish.automessaging.configuration.JasyptEncryptorConfiguration;
import com.krish.automessaging.configuration.SpringMailConfiguration;
import com.krish.automessaging.datamodel.record.EmailOptionsRecord;
import com.krish.automessaging.service.impl.EmailServiceImpl;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Profile(value = "dev")
@ContextConfiguration(classes = { EmailServiceImpl.class, JasyptEncryptorConfiguration.class,
        SpringMailConfiguration.class })
@EnableEncryptableProperties
@Slf4j
public class EmailServiceTest {

    @Autowired
    private EmailServiceImpl emailService;

    @Test
    public void sendEmailTest() throws MessagingException {
        EmailOptionsRecord record = EmailOptionsRecord.builder().toName("Venkatt").fromEmail("Venkat@gmail.com")
                .toEmail("venkata.krishna@kloudspot.com").subject("test mail").type(EmailOptionsRecord.TYPE_TEXT)
                .message("Test Email upon mvn build").build();
        emailService.sendEmail(record);
    }
}
