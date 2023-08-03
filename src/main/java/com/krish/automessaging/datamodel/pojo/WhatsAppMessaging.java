package com.krish.automessaging.datamodel.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Hash code.
 *
 * @return the int
 */
@Data

/**
 * To string.
 *
 * @return the java.lang. string
 */
@ToString

/**
 * Instantiates a new whats app messaging.
 */
@NoArgsConstructor

/**
 * Instantiates a new whats app messaging.
 *
 * @param id
 *            the id
 * @param from
 *            the from
 * @param to
 *            the to
 * @param messages
 *            the messages
 */
@AllArgsConstructor

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Builder
public class WhatsAppMessaging {

    /**
     * The Enum Type.
     */
    public enum Type {

        /** The auto. */
        AUTO,
        /** The manual. */
        MANUAL
    }

    /** The id. */
    private String id;

    /** The from. */
    private String from;

    /** The to. */
    private String to;

    /** The messages. */
    private List<Message> messages = new ArrayList<>();

    /**
     * Hash code.
     *
     * @return the int
     */
    @Data

    /**
     * To string.
     *
     * @return the java.lang. string
     */
    @ToString

    /**
     * Instantiates a new message.
     */
    @NoArgsConstructor

    /**
     * Instantiates a new message.
     *
     * @param type
     *            the type
     * @param timestamp
     *            the timestamp
     * @param message
     *            the message
     */
    @AllArgsConstructor

    /**
     * To string.
     *
     * @return the java.lang. string
     */
    @Builder
    public static class Message {

        /** The type. */
        private String type = Type.AUTO.toString();

        /** The timestamp. */
        private long timestamp;

        /** The message. */
        private String message;
    }
}
