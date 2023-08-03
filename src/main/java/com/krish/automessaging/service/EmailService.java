package com.krish.automessaging.service;

import com.krish.automessaging.datamodel.record.EmailOptionsRecord;

import jakarta.mail.MessagingException;

/**
 * The Interface EmailService.
 */
public interface EmailService {

    /**
     * Send email.
     *
     * @param emailOptionsRecord
     *            the email options record
     *
     * @throws MessagingException
     *             the messaging exception
     */
    public void sendEmail(EmailOptionsRecord emailOptionsRecord) throws MessagingException;

}
