package com.krish.automessaging.datamodel.pojo;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppMessaging {
    public enum Type {
        AUTO, MANUAL
    }

    private String id;
    private String from;
    private String to;
    private List<Message> messages = new ArrayList<>();

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String type = Type.AUTO.toString();
        private long timestamp;
        private String message;
    }
}
