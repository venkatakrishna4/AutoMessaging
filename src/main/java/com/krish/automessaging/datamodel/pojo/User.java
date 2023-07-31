package com.krish.automessaging.datamodel.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.krish.automessaging.datamodel.pojo.es.PersistenceAudit;
import lombok.*;

import java.util.List;

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
