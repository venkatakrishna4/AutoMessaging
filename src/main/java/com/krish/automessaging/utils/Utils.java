package com.krish.automessaging.utils;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

/**
 * The type Utils.
 */
@Component
public class Utils {
    /**
     * Generate uuid string.
     *
     * @return the string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
