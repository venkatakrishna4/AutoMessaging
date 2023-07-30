package com.krish.automessaging.resource;

import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import com.krish.automessaging.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    @PreAuthorize("hasAuthority('PRIVILEGE_USER_GET')")
    public ResponseEntity<UserResponseRecord> getUser(
            @NotBlank(message = "Valid ID required to find user") @PathVariable String id) throws IOException {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRIVILEGE_USER_GET')")
    public ResponseEntity<PaginatedResponseRecord<List<UserResponseRecord>>> getUsers() throws IOException {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PatchMapping
    @PreAuthorize("hasAuthority('PRIVILEGE_USER_UPDATE')")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserRequestRecord userRequestRecord,
            HttpServletRequest servletRequest) throws IOException {
        return new ResponseEntity<>(userService.updateUser(userRequestRecord, servletRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRIVILEGE_USER_DELETE')")
    public ResponseEntity<Object> deleteUser(
            @NotBlank(message = "Valid ID required to delete user") @PathVariable String id,
            HttpServletRequest servletRequest) throws IOException {
        userService.deleteUser(id, servletRequest);
        return ResponseEntity.noContent().build();
    }
}
