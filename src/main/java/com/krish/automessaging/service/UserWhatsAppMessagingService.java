package com.krish.automessaging.service;

import com.krish.automessaging.datamodel.record.WhatsAppMessagingRecord;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface UserWhatsAppMessagingService {
    String saveWhatsAppMessaging(WhatsAppMessagingRecord whatsAppMessagingRecord, HttpServletRequest servletRequest)
            throws IOException;

    WhatsAppMessagingRecord getWhatsAppMessaging(String id) throws IOException;

    void deleteWhatsAppMessaging(String id, HttpServletRequest servletRequest) throws IOException;
}
