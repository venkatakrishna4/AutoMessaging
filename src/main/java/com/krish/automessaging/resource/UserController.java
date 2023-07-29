package com.krish.automessaging.resource;

import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/public")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequestRecord userRequestRecord)
            throws IOException {
        return new ResponseEntity<>(userService.createUser(userRequestRecord), HttpStatus.CREATED);
    }
}
