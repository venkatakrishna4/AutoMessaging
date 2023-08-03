package com.krish.automessaging.exception.custom;

import lombok.Getter;
import lombok.Setter;

/**
 * Gets the message.
 *
 * @return the message
 */
@Getter
@Setter
public class RecordNotFoundException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The message. */
    private final String message;

    /**
     * Instantiates a new record not found exception.
     *
     * @param message
     *            the message
     */
    public RecordNotFoundException(final String message) {
        this.message = message;
    }
}
