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

/**
 * The Class UserController.
 */
@RestController
@RequestMapping(value = "/api/v1/user")
@Validated
public class UserController {

    /** The user service. */
    private final UserService userService;

    /**
     * Instantiates a new user controller.
     *
     * @param userService
     *            the user service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates the user.
     *
     * @param userRequestRecord
     *            the user request record
     * @param servletRequest
     *            the servlet request
     *
     * @return the response entity
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @PostMapping(value = "/public")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequestRecord userRequestRecord,
            HttpServletRequest servletRequest) throws IOException {
        return new ResponseEntity<>(userService.createUser(userRequestRecord, servletRequest), HttpStatus.CREATED);
    }

    /**
     * Gets the user.
     *
     * @param id
     *            the id
     *
     * @return the user
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyAuthority({'PRIVILEGE_USER_GET', 'ROLE_ADMIN'})")
    public ResponseEntity<UserResponseRecord> getUser(
            @NotBlank(message = "Valid ID required to find user") @PathVariable String id) throws IOException {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    /**
     * Gets the users.
     *
     * @return the users
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority({'PRIVILEGE_USER_GET', 'ROLE_ADMIN'})")
    public ResponseEntity<PaginatedResponseRecord<List<UserResponseRecord>>> getUsers() throws IOException {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    /**
     * Update user.
     *
     * @param userRequestRecord
     *            the user request record
     * @param servletRequest
     *            the servlet request
     *
     * @return the response entity
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @PatchMapping
    @PreAuthorize("hasAnyAuthority({'PRIVILEGE_USER_UPDATE', 'ROLE_ADMIN'})")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserRequestRecord userRequestRecord,
            HttpServletRequest servletRequest) throws IOException {
        return new ResponseEntity<>(userService.updateUser(userRequestRecord, servletRequest), HttpStatus.OK);
    }

    /**
     * Delete user.
     *
     * @param id
     *            the id
     * @param servletRequest
     *            the servlet request
     *
     * @return the response entity
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority({'ROLE_ADMIN'})")
    public ResponseEntity<Object> deleteUser(
            @NotBlank(message = "Valid ID required to delete user") @PathVariable String id,
            HttpServletRequest servletRequest) throws IOException {
        userService.deleteUser(id, servletRequest);
        return ResponseEntity.noContent().build();
    }
}
