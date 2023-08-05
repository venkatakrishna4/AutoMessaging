package com.krish.automessaging.datamodel.record;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The type User response record.
 */

/**
 * To string.
 *
 * @return the java.lang. string
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserResponseRecord(String id, String name, String username, String email, List<String> roles,
        String phone) {
}
