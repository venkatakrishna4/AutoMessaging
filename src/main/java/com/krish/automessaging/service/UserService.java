package com.krish.automessaging.service;

import com.krish.automessaging.datamodel.pojo.User;
import com.krish.automessaging.datamodel.record.PaginatedResponseRecord;
import com.krish.automessaging.datamodel.record.UserRequestRecord;
import com.krish.automessaging.datamodel.record.UserResponseRecord;

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
     *            the user request record
     *
     * @return the string
     */
    String createUser(UserRequestRecord userRequestRecord) throws IOException;

    /**
     * Gets user.
     *
     * @param id
     *            the id
     *
     * @return the user
     */
    UserResponseRecord getUser(String id);

    /**
     * Gets all users.
     *
     * @return the all users
     */
    PaginatedResponseRecord<List<UserResponseRecord>> getAllUsers();

    /**
     * Update user string.
     *
     * @param userRequestRecord
     *            the user request record
     *
     * @return the string
     */
    String updateUser(UserRequestRecord userRequestRecord);

    /**
     * Delete user.
     *
     * @param id
     *            the id
     */
    void deleteUser(String id);
}
