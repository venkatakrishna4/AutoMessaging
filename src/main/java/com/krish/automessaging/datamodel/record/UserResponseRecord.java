package com.krish.automessaging.datamodel.record;

import lombok.Builder;

import java.util.List;

/**
 * The type User response record.
 */
@Builder
public record UserResponseRecord(String id, String name, String username, String email, List<String> roles,
        String phone) {
}
