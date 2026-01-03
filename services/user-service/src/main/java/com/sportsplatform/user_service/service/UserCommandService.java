package com.sportsplatform.user_service.service;

import com.sportsplatform.user_service.dto.CreateUserRequest;
import com.sportsplatform.user_service.exception.UserAlreadyExistsException;
import com.sportsplatform.user_service.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sportsplatform.user.jooq.tables.Roles.ROLES;
import static com.sportsplatform.user.jooq.tables.UserRoles.USER_ROLES;
import static com.sportsplatform.user.jooq.tables.Users.USERS;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService {

    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProjection createUser(
            CreateUserRequest createUserRequest
    ) {
        String username=createUserRequest.getUsername();
        String email=createUserRequest.getEmail();
        String rawPassword=createUserRequest.getPassword();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        String userCode=generateUserCode();

        try {
            dsl.insertInto(USERS)
                    .set(USERS.ID, userId)
                    .set(USERS.USERNAME, username)
                    .set(USERS.EMAIL, email)
                    .set(USERS.PASSWORD_HASH, passwordEncoder.encode(rawPassword))
                    .set(USERS.STATUS, "ACTIVE")
                    .set(USERS.LAST_LOGIN_AT, LocalDateTime.now())
                    .set(USERS.USER_CODE,userCode)
                    .execute();
            assignRoles(userId, List.of("PLAYER"));

            return new UserProjection(
                    userCode,
                    username,
                    email,
                    "ACTIVE",
                    String.valueOf(now),      // createdAt (or fetch from DB)
                    String.valueOf(now)       // lastLoginAt
            );

        } catch (DuplicateKeyException ex) {
            if (ex.getMessage().contains("users_email_key")) {
                throw new UserAlreadyExistsException("Email already registered");
            }
            if (ex.getMessage().contains("users_username_key")) {
                throw new UserAlreadyExistsException("Username already taken");
            }
            throw ex;
        }
    }

    @Transactional
    public void assignRoles(UUID userId, List<String> roleNames) {
        dsl.insertInto(USER_ROLES, USER_ROLES.USER_ID, USER_ROLES.ROLE_ID)
                .select(
                        DSL.select(
                                        DSL.val(userId),
                                        ROLES.ID
                                )
                                .from(ROLES)
                                .where(ROLES.NAME.in(roleNames))
                )
                .execute();
    }

    private static final AtomicInteger counter = new AtomicInteger(1);

    private String generateUserCode() {
        return "USR" + String.format("%06d", counter.getAndIncrement());
    }
}
