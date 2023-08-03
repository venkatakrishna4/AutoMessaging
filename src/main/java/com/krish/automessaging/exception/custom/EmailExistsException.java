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
public class EmailExistsException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The message. */
    private final String message;

    /**
     * Instantiates a new email exists exception.
     *
     * @param message
     *            the message
     */
    public EmailExistsException(String message) {
        this.message = message;
    }

}
