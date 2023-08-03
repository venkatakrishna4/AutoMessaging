package com.krish.automessaging.service.impl;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import com.krish.automessaging.datamodel.record.EmailOptionsRecord;
import com.krish.automessaging.exception.custom.TooManyRequestsException;
import com.krish.automessaging.service.EmailService;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class EmailServiceImpl.
 */
@Service

/** The Constant log. */
@Slf4j
public class EmailServiceImpl implements EmailService {

    /** The mail sender. */
    private JavaMailSender mailSender;

    /** The template engine. */
    private TemplateEngine templateEngine;

    /** The email buckets. */
    private final Map<String, Bucket> emailBuckets = new ConcurrentHashMap<>();

    /**
     * Instantiates a new email service impl.
     *
     * @param mailSender
     *            the mail sender
     * @param templateEngine
     *            the template engine
     */
    @Autowired
    public EmailServiceImpl(final JavaMailSender mailSender, final TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Send email.
     *
     * @param emailOptionsRecord
     *            the email options record
     *
     * @throws MessagingException
     *             the messaging exception
     */
    @Override
    @Async
    public void sendEmail(EmailOptionsRecord emailOptionsRecord) throws MessagingException {
        log.debug("Sending Email Initated to {}", emailOptionsRecord.toEmail());

        if (isNotBucketAvailable(emailOptionsRecord.toEmail())) {
            throw new TooManyRequestsException("Cannot send Email right now, please try again later");
        }

        // // FIXME: fix these Context variables... Need to configure appropriately based on the thymeleaf template
        // final Context ctx = new Context();
        // ctx.setVariable("name", emailOptionsRecord.toName());
        // ctx.setVariable("subscriptionDate", new Date());
        // ctx.setVariable("hobbies", List.of("Badminton"));
        // ctx.setVariable("imageResourceName", emailOptionsRecord.imageResourceName());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setSubject(emailOptionsRecord.subject());
        message.setFrom(emailOptionsRecord.fromEmail());
        message.setTo(emailOptionsRecord.toEmail());

        if (EmailOptionsRecord.TYPE_TEXT.equals(emailOptionsRecord.type())) {
            message.setText(emailOptionsRecord.message(), false);
        } else {
            final String htmlContent = this.templateEngine
                    .process("thymeleaf_templates/" + emailOptionsRecord.fileName(), emailOptionsRecord.context());
            message.setText(htmlContent, true);
        }

        // Send mail
        this.mailSender.send(mimeMessage);
        log.debug("Email has been sent successfully to {}", emailOptionsRecord.toEmail());

    }

    /**
     * Checks if is not bucket available.
     *
     * @param email
     *            the email
     *
     * @return true, if is not bucket available
     */
    private boolean isNotBucketAvailable(String email) {
        Bucket bucket = emailBuckets.computeIfAbsent(email, key -> createBucket());
        log.debug(email + "->" + bucket.getAvailableTokens());
        return !bucket.tryConsume(1);
    }

    /**
     * Creates the bucket.
     *
     * @return the bucket
     */
    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.simple(1, Duration.ofMinutes(1));
        return Bucket.builder().addLimit(limit).build();
    }
}
