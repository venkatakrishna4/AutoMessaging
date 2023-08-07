package com.krish.automessaging.service;

import java.net.URISyntaxException;

import com.krish.automessaging.datamodel.record.WhatsAppOptionsRecord;

/**
 * The Interface WhatsAppService.
 */
public interface WhatsAppService {

    /**
     * Send message.
     *
     * @param whatsAppMessagingRecord
     *            the whats app messaging record
     */
    void sendMessage(WhatsAppOptionsRecord whatsAppOptionsRecord) throws URISyntaxException;
}
