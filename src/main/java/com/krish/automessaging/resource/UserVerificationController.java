package com.krish.automessaging.resource;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krish.automessaging.service.UserVerificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/user/v1/verification")
@Validated
public class UserVerificationController {

    private final UserVerificationService userVerificationService;

    @Autowired
    public UserVerificationController(UserVerificationService userVerificationService) {
        this.userVerificationService = userVerificationService;
    }

    @GetMapping("/email/{key}")
    public ResponseEntity<Object> verifyUserByEmail(
            @NotBlank(message = "Invalid Verification") @PathVariable String key, HttpServletRequest servletRequest)
            throws IOException {
        return ResponseEntity.ok(userVerificationService.verifyByEmail(key, servletRequest));
    }

}
