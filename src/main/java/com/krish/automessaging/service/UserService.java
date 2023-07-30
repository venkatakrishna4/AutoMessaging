package com.krish.automessaging.service;

import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

/**
 * The interface User service.
 */
public interface UserService extends BaseServiceProvider<String> {
    /**
     * Create user string.
     *
     * @param userRequestRecord
     *            the user servletRequest record
     *
     * @return the string
     */
    String createUser(UserRequestRecord userRequestRecord, HttpServletRequest servletRequest) throws IOException;

    /**
     * Gets user.
     *
     * @param id
     *            the id
     *
     * @return the user
     */
    UserResponseRecord getUser(String id) throws IOException;

    /**
     * Gets all users.
     *
     * @return the all users
     */
    PaginatedResponseRecord<List<UserResponseRecord>> getAllUsers() throws IOException;

    /**
     * Update user string.
     *
     * @param userRequestRecord
     *            the user servletRequest record
     *
     * @return the string
     */
    String updateUser(UserRequestRecord userRequestRecord, HttpServletRequest servletRequest) throws IOException;

    /**
     * Delete user.
     *
     * @param id
     *            the id
     */
    void deleteUser(String id, HttpServletRequest servletRequest) throws IOException;
}
