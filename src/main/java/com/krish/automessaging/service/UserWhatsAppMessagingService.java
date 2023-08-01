package com.krish.automessaging.service;

import java.io.IOException;

import com.krish.automessaging.datamodel.record.WhatsAppMessagingRecord;

import jakarta.servlet.http.HttpServletRequest;

public interface UserWhatsAppMessagingService {
    String saveWhatsAppMessaging(WhatsAppMessagingRecord whatsAppMessagingRecord, HttpServletRequest servletRequest)
            throws IOException;

    WhatsAppMessagingRecord getWhatsAppMessaging(String userId, String id) throws IOException;

    void deleteWhatsAppMessaging(String userId, String id, HttpServletRequest servletRequest) throws IOException;
}
