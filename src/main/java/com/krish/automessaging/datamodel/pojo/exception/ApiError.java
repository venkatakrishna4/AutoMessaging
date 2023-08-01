package com.krish.automessaging.datamodel.pojo.exception;

import java.util.List;

import org.springframework.http.HttpStatusCode;

import lombok.Builder;

@Builder
public record ApiError(String message, HttpStatusCode status, String timestamp, List<ApiSubErrors> subErrors) {
}
