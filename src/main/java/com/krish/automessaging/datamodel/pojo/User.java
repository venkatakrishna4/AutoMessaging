package com.krish.automessaging.datamodel.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.krish.automessaging.datamodel.pojo.es.PersistenceAudit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The Class User.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * Hash code.
 *
 * @return the int
 */
@EqualsAndHashCode(callSuper = true)

/**
 * Gets the whats app messaging.
 *
 * @return the whats app messaging
 */
@Getter

/**
 * Sets the whats app messaging.
 *
 * @param whatsAppMessaging
 *            the new whats app messaging
 */
@Setter

/**
 * Instantiates a new user.
 */
@RequiredArgsConstructor

/**
 * Instantiates a new user.
 *
 * @param id
 *            the id
 * @param name
 *            the name
 * @param username
 *            the username
 * @param password
 *            the password
 * @param email
 *            the email
 * @param disabled
 *            the disabled
 * @param roles
 *            the roles
 * @param passwordResetKey
 *            the password reset key
 * @param phone
 *            the phone
 * @param loggedInTime
 *            the logged in time
 * @param ttlEnabled
 *            the ttl enabled
 * @param whatsAppMessaging
 *            the whats app messaging
 */
@AllArgsConstructor

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Builder

/**
 * To string.
 *
 * @return the java.lang. string
 */
@ToString(callSuper = true)
public class User extends PersistenceAudit<User> {

    /** The id. */
    private String id;

    /** The name. */
    private String name;

    /** The username. */
    private String username;

    /** The password. */
    private String password;

    /** The email. */
    private String email;

    /** The disabled. */
    private boolean disabled;

    /** The roles. */
    private List<String> roles = List.of("USER");

    /** The password reset key. */
    private String passwordResetKey;

    /** The phone. */
    private String phone;

    /** The logged in time. */
    private long loggedInTime = System.currentTimeMillis();

    /** The ttl enabled. */
    private boolean ttlEnabled = true;

    /** The whats app messaging. */
    private WhatsAppMessaging whatsAppMessaging = new WhatsAppMessaging();
}
