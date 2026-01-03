package com.sportsplatform.auth_service.service;

import com.sportsplatform.auth_service.dto.LoginResult;
import org.jooq.DSLContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sportsplatform.user.jooq.tables.Roles.ROLES;
import static com.sportsplatform.user.jooq.tables.UserRoles.USER_ROLES;
import static com.sportsplatform.user.jooq.tables.Users.USERS;

@Service
@
public class AuthService {

    private final DSLContext dsl;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResult login(String usernameOrEmail, String rawPassword) {

        var user = dsl.selectFrom(USERS)
                .where(
                        USERS.USERNAME.eq(usernameOrEmail)
                                .or(USERS.EMAIL.eq(usernameOrEmail))
                )
                .fetchOne();

        if (user == null) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        List<String> roles = dsl
                .select(ROLES.NAME)
                .from(USER_ROLES)
                .join(ROLES).on(USER_ROLES.ROLE_ID.eq(ROLES.ID))
                .where(USER_ROLES.USER_ID.eq(user.getId()))
                .fetchInto(String.class);

        return new LoginResult(
                user.getId(),
                user.getUsername(),
                roles
        );
    }
}
