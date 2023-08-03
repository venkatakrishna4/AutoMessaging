package com.krish.automessaging.exception.custom;

import lombok.Getter;
import lombok.Setter;

/**
 * Gets the message.
 *
 * @return the message
 */
@Getter

/**
 * Sets the message.
 *
 * @param message
 *            the new message
 */
@Setter
public class TooManyRequestsException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The message. */
    private String message;

    /**
     * Instantiates a new too many requests exception.
     *
     * @param message
     *            the message
     */
    public TooManyRequestsException(String message) {
        this.message = message;
    }

}
