package com.krish.automessaging.datamodel.pojo;

import com.krish.automessaging.datamodel.pojo.es.PersistenceAudit;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User extends PersistenceAudit<User> {
    private String id;
    private String name;
    private String password;
    private String email;
    private boolean disabled;
    private final List<String> roles = List.of("USER");
    private String passwordResetKey;
    private String phone;
    private long loggedInTime = System.currentTimeMillis();
    private boolean ttlEnabled;
}
