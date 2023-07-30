package com.krish.automessaging.datamodel.pojo.exception;

import lombok.Builder;
import org.springframework.http.HttpStatusCode;

import java.util.List;

@Builder
public record ApiError(String message, HttpStatusCode status, String timestamp, List<ApiSubErrors> subErrors) {
}
