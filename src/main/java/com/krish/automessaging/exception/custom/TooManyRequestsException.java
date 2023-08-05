package com.krish.automessaging.exception.custom;

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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
