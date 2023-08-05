package com.krish.automessaging.datamodel.pojo.exception;

import java.util.List;

import org.springframework.http.HttpStatusCode;

/**
 * To string.
 *
 * @return the java.lang. string
 */
public record ApiError(String message, HttpStatusCode status, String timestamp, List<ApiSubErrors> subErrors) {
}
