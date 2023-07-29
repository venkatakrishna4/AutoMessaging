package com.krish.automessaging.datamodel.pojo.exception;

import lombok.Builder;

@Builder
public record ApiSubErrors(String field, String message) {
}
