package com.krish.automessaging.datamodel.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.krish.automessaging.datamodel.pojo.WhatsAppMessaging;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WhatsAppMessagingRecord(
        @NotBlank(message = "User ID is required to save WhatsApp Messaging") String userId, String id, String from,
        String to, List<WhatsAppMessaging.Message> messages) {
}
