package com.krish.automessaging.datamodel.record;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.krish.automessaging.datamodel.pojo.WhatsAppMessaging;

import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WhatsAppMessagingRecord(
        @NotBlank(message = "User ID is required to save WhatsApp Messaging") String userId, String id, String from,
        String to, List<WhatsAppMessaging.Message> messages) {
}
