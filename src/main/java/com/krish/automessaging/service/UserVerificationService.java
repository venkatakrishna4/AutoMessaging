package com.krish.automessaging.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

public interface UserVerificationService {

    String verifyByEmail(String key, HttpServletRequest request) throws IOException;
}
