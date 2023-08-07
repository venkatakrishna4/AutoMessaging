package com.krish.automessaging.datamodel.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@link WhatsAppMessaging} has WhatsApp Messaging information to be used for the Automation processing
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class WhatsAppMessaging {

    public static final long MINUTE_INTERVAL = 60000;
    public static final long HOURLY_INTERVAL = 60 * MINUTE_INTERVAL;
    public static final long DAILY_INTERVAL = 24 * HOURLY_INTERVAL;
    public static final long WEEKLY_INTERVAL = 7 * DAILY_INTERVAL;
    public static final long MONTHLY_INTERVAL = 4 * WEEKLY_INTERVAL;
    public static final long YEARLY_INTERVAL = 12 * MONTHLY_INTERVAL;

    public enum MessageType {
        text, template
    }

    public enum FrequencyType {
        AUTO, MANUAL
    }

    public enum Frequency {
        MINUTE, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String type = MessageType.text.toString();
        private String frequencyType = FrequencyType.MANUAL.toString();
        private String frequency = Frequency.MINUTE.toString();
        private String to;
        private long timestamp;
        private String message;

        public Message() {
        }

        public Message(String type, String frequencyType, String frequency, String to, long timestamp, String message) {
            super();
            this.type = type;
            this.frequencyType = frequencyType;
            this.frequency = frequency;
            this.to = to;
            this.timestamp = timestamp;
            this.message = message;
        }

        public String getFrequencyType() {
            return frequencyType;
        }

        public void setFrequencyType(String frequencyType) {
            this.frequencyType = frequencyType;
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

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

    }
}
