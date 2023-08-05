package com.krish.automessaging.datamodel.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link WhatsAppMessaging} has WhatsApp Messaging information to be used for the Automation processing
 */

public class WhatsAppMessaging {
    public enum Type {
        AUTO, MANUAL
    }

    private String id;
    private String from;
    private List<Message> messages = new ArrayList<>();

    public WhatsAppMessaging() {
    }

    public WhatsAppMessaging(String id, String from, List<Message> messages) {
        super();
        this.id = id;
        this.from = from;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public static class Message {
        private String type = Type.AUTO.toString();
        private String to;
        private long timestamp;
        private String message;

        public Message() {
        }

        public Message(String type, String to, long timestamp, String message) {
            super();
            this.type = type;
            this.to = to;
            this.timestamp = timestamp;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
