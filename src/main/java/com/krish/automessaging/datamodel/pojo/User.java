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

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class User extends PersistenceAudit<User> {
    private String id;
    private String name;
    private String username;
    private String password;
    private String email;
    private boolean disabled;
    private List<String> roles = List.of("USER");
    private String passwordResetKey;
    private String phone;
    private long loggedInTime = System.currentTimeMillis();
    private boolean ttlEnabled = true;
    private WhatsAppMessaging whatsAppMessaging = new WhatsAppMessaging();
}
