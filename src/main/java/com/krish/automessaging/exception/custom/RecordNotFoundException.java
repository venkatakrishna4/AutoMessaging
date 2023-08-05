package com.krish.automessaging.exception.custom;

/**
 * Gets the message.
 *
 * @return the message
 */

public class RecordNotFoundException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The message. */
    private String message;

    /**
     * Instantiates a new record not found exception.
     *
     * @param message
     *            the message
     */
    public RecordNotFoundException(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
