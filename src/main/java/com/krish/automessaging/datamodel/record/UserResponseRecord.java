package com.krish.automessaging.datamodel.record;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;

/**
 * The type User response record.
 */

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserResponseRecord(String id, String name, String username, String email, List<String> roles,
        String phone) {
}
