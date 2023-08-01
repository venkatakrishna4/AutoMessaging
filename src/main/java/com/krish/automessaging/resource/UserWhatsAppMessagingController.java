package com.krish.automessaging.resource;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krish.automessaging.datamodel.record.WhatsAppMessagingRecord;
import com.krish.automessaging.service.UserWhatsAppMessagingService;
import com.krish.automessaging.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/user/whatsapp")
public class UserWhatsAppMessagingController {

    private final UserWhatsAppMessagingService userWhatsAppMessagingService;

    @Autowired
    public UserWhatsAppMessagingController(final UserWhatsAppMessagingService userWhatsAppMessagingService,
            final Utils utils) {
        this.userWhatsAppMessagingService = userWhatsAppMessagingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority({'PRIVILEGE_USER_WHATSAPP_SAVE', 'ROLE_ADMIN'})")
    public ResponseEntity<String> createWhatsAppMessaging(
            @Valid @RequestBody WhatsAppMessagingRecord whatsAppMessagingRecord, HttpServletRequest servletRequest)
            throws IOException {
        return new ResponseEntity<>(
                userWhatsAppMessagingService.saveWhatsAppMessaging(whatsAppMessagingRecord, servletRequest),
                HttpStatus.CREATED);
    }

    @GetMapping("/{user-id}/{id}")
    @PreAuthorize("hasAnyAuthority({'PRIVILEGE_USER_WHATSAPP_GET', 'ROLE_ADMIN'})")
    public ResponseEntity<WhatsAppMessagingRecord> getWhatsAppMessaging(
            @NotBlank(message = "Valid User ID is required") @PathVariable("user-id") String userId, @NotBlank(message = "Valid ID is required") @PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(userWhatsAppMessagingService.getWhatsAppMessaging(userId, id));
    }

    @DeleteMapping("/{user-id}/{id}")
    @PreAuthorize("hasAnyAuthority('PRIVILEGE_USER_WHATSAPP_DELETE', 'ROLE_ADMIN')")
    public ResponseEntity<Object> deleteWhatsAppMessaging(
            @NotBlank(message = "Valid User ID is required") @PathVariable("user-id") String userId, @NotBlank(message = "Valid ID is required") @PathVariable("id") String id, HttpServletRequest servletRequest)
            throws IOException {
        userWhatsAppMessagingService.deleteWhatsAppMessaging(userId, id, servletRequest);
        return ResponseEntity.noContent().build();
    }

}
