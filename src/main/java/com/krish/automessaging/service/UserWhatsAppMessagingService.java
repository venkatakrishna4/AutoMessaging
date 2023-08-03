package com.krish.automessaging.service;

import java.io.IOException;

import com.krish.automessaging.datamodel.record.WhatsAppMessagingRecord;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Interface UserWhatsAppMessagingService.
 */
public interface UserWhatsAppMessagingService {

    /**
     * Save whats app messaging.
     *
     * @param whatsAppMessagingRecord
     *            the whats app messaging record
     * @param servletRequest
     *            the servlet request
     *
     * @return the string
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    String saveWhatsAppMessaging(WhatsAppMessagingRecord whatsAppMessagingRecord, HttpServletRequest servletRequest)
            throws IOException;

    /**
     * Gets the whats app messaging.
     *
     * @param userId
     *            the user id
     * @param id
     *            the id
     *
     * @return the whats app messaging
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    WhatsAppMessagingRecord getWhatsAppMessaging(String userId, String id) throws IOException;

    /**
     * Delete whats app messaging.
     *
     * @param userId
     *            the user id
     * @param id
     *            the id
     * @param servletRequest
     *            the servlet request
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void deleteWhatsAppMessaging(String userId, String id, HttpServletRequest servletRequest) throws IOException;
}
