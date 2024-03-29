package com.krish.automessaging.exception.custom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailExistsException extends RuntimeException {
    private final String message;

    public EmailExistsException(String message) {
        this.message = message;
    }

}
