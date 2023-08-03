package com.krish.automessaging.datamodel.pojo.exception;

import lombok.Builder;

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Builder
public record ApiSubErrors(String field, String message) {
}
