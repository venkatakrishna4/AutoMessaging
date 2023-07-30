package com.krish.automessaging.datamodel.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

/**
 * The type User response record.
 */
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserResponseRecord(String id, String name, String username, String email, List<String> roles,
        String phone) {
}
