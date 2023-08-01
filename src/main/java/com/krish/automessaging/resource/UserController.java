package com.krish.automessaging.resource;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import com.krish.automessaging.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping(value = "/api/v1/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/public")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequestRecord userRequestRecord,
            HttpServletRequest servletRequest) throws IOException {
        return new ResponseEntity<>(userService.createUser(userRequestRecord, servletRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'PRIVILEGE_USER_GET', 'ROLE_ADMIN'})")
    public ResponseEntity<UserResponseRecord> getUser(
            @NotBlank(message = "Valid ID required to find user") @PathVariable String id) throws IOException {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority({'PRIVILEGE_USER_GET', 'ROLE_ADMIN'})")
    public ResponseEntity<PaginatedResponseRecord<List<UserResponseRecord>>> getUsers() throws IOException {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority({'PRIVILEGE_USER_UPDATE', 'ROLE_ADMIN'})")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserRequestRecord userRequestRecord,
            HttpServletRequest servletRequest) throws IOException {
        return new ResponseEntity<>(userService.updateUser(userRequestRecord, servletRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'ROLE_ADMIN'})")
    public ResponseEntity<Object> deleteUser(
            @NotBlank(message = "Valid ID required to delete user") @PathVariable String id,
            HttpServletRequest servletRequest) throws IOException {
        userService.deleteUser(id, servletRequest);
        return ResponseEntity.noContent().build();
    }
}
