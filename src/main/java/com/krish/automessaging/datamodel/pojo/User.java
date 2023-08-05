package com.krish.automessaging.datamodel.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.krish.automessaging.datamodel.pojo.es.PersistenceAudit;

/**
 * {@link User} class has all the user related information for the Automation Messaging service
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends PersistenceAudit<User> {

    private String id;
    private String name;
    private String username;
    private String password;
    private String email;
    private boolean disabled;
    private boolean activated;
    private List<String> roles = List.of("USER");
    private String passwordResetKey;
    private String phone;
    private long loggedInTime = System.currentTimeMillis();
    private boolean ttlEnabled = true;
    private WhatsAppMessaging whatsAppMessaging = new WhatsAppMessaging();

    public User() {
    }

    public User(String id, String name, String username, String password, String email, boolean disabled,
            boolean activated, List<String> roles, String passwordResetKey, String phone, long loggedInTime,
            boolean ttlEnabled, WhatsAppMessaging whatsAppMessaging) {
        super();
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.disabled = disabled;
        this.activated = activated;
        this.roles = roles;
        this.passwordResetKey = passwordResetKey;
        this.phone = phone;
        this.loggedInTime = loggedInTime;
        this.ttlEnabled = ttlEnabled;
        this.whatsAppMessaging = whatsAppMessaging;
    }

    public User(Builder builder) {
        super();
        this.id = builder.id;
        this.name = builder.name;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.disabled = builder.disabled;
        this.activated = builder.activated;
        this.roles = builder.roles;
        this.passwordResetKey = builder.passwordResetKey;
        this.phone = builder.phone;
        this.loggedInTime = builder.loggedInTime;
        this.ttlEnabled = builder.ttlEnabled;
        this.whatsAppMessaging = builder.whatsAppMessaging;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getPasswordResetKey() {
        return passwordResetKey;
    }

    public void setPasswordResetKey(String passwordResetKey) {
        this.passwordResetKey = passwordResetKey;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getLoggedInTime() {
        return loggedInTime;
    }

    public void setLoggedInTime(long loggedInTime) {
        this.loggedInTime = loggedInTime;
    }

    public boolean isTtlEnabled() {
        return ttlEnabled;
    }

    public void setTtlEnabled(boolean ttlEnabled) {
        this.ttlEnabled = ttlEnabled;
    }

    public WhatsAppMessaging getWhatsAppMessaging() {
        return whatsAppMessaging;
    }

    public void setWhatsAppMessaging(WhatsAppMessaging whatsAppMessaging) {
        this.whatsAppMessaging = whatsAppMessaging;
    }

    public static class Builder {
        private String id;
        private String name;
        private String username;
        private String password;
        private String email;
        private boolean disabled;
        private boolean activated;
        private List<String> roles = List.of("USER");
        private String passwordResetKey;
        private String phone;
        private long loggedInTime = System.currentTimeMillis();
        private boolean ttlEnabled = true;
        private WhatsAppMessaging whatsAppMessaging = new WhatsAppMessaging();

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setDisabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder setActivated(boolean activated) {
            this.activated = activated;
            return this;
        }

        public Builder setRoles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder setPasswordResetKey(String passwordResetKey) {
            this.passwordResetKey = passwordResetKey;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setLoggedInTime(long loggedInTime) {
            this.loggedInTime = loggedInTime;
            return this;
        }

        public Builder setTtlEnabled(boolean ttlEnabled) {
            this.ttlEnabled = ttlEnabled;
            return this;
        }

        public Builder setWhatsAppMessaging(WhatsAppMessaging whatsAppMessaging) {
            this.whatsAppMessaging = whatsAppMessaging;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public long getCreatedTime() {

        return super.getCreatedTime();
    }

    @Override
    public void setCreatedTime(Long createdTime) {

        super.setCreatedTime(createdTime);
    }

    @Override
    public String getCreatedBy() {

        return super.getCreatedBy();
    }

    @Override
    public void setCreatedBy(String createdBy) {

        super.setCreatedBy(createdBy);
    }

    @Override
    public String getLastUpdatedBy() {

        return super.getLastUpdatedBy();
    }

    @Override
    public void setLastUpdatedBy(String lastUpdatedBy) {

        super.setLastUpdatedBy(lastUpdatedBy);
    }

    @Override
    public Long getLastUpdatedTime() {

        return super.getLastUpdatedTime();
    }

    @Override
    public void setLastUpdatedTime(Long lastUpdatedTime) {

        super.setLastUpdatedTime(lastUpdatedTime);
    }

    @Override
    public int compareTo(User o) {

        return super.compareTo(o);
    }

    @Override
    public boolean equals(Object o) {

        return super.equals(o);
    }

    @Override
    public int hashCode() {

        return super.hashCode();
    }

}
