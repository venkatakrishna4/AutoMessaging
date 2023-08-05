package com.krish.automessaging.service;

import java.io.IOException;
import java.util.List;

import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The interface User service.
 */
public interface UserService extends BaseServiceProvider<User> {

    /**
     * Create user string.
     *
     * @param userRequestRecord
     *            the user servletRequest record
     * @param servletRequest
     *            the servlet request
     *
     * @return the string
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    String createUser(UserRequestRecord userRequestRecord, HttpServletRequest servletRequest) throws IOException;

    /**
     * Gets user.
     *
     * @param id
     *            the id
     *
     * @return the user
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    UserResponseRecord getUser(String id) throws IOException;

    /**
     * Gets all users.
     *
     * @return the all users
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    PaginatedResponseRecord<List<UserResponseRecord>> getAllUsers() throws IOException;

    /**
     * Update user string.
     *
     * @param userRequestRecord
     *            the user servletRequest record
     * @param servletRequest
     *            the servlet request
     *
     * @return the string
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    String updateUser(UserRequestRecord userRequestRecord, HttpServletRequest servletRequest) throws IOException;

    /**
     * Delete user.
     *
     * @param id
     *            the id
     * @param servletRequest
     *            the servlet request
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void deleteUser(String id, HttpServletRequest servletRequest) throws IOException;
}
