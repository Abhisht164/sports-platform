package com.sportsplatform.user_service.service;

import com.sportsplatform.user_service.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sportsplatform.user.jooq.tables.Users.USERS;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;
    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;

    /**
     * Fetch all users from DB.
     * Pure jOOQ logic – no GraphQL types here.
     */
    @Transactional(readOnly = true)
    public List<UserProjection> fetchUsers(Integer limit, Integer offset) {
        int safeLimit = sanitizeLimit(limit);
        int safeOffset = sanitizeOffset(offset);
        return dsl
                .select(
                        USERS.USER_CODE,
                        USERS.USERNAME,
                        USERS.EMAIL,
                        USERS.STATUS,
                        USERS.CREATED_AT,
                        USERS.LAST_LOGIN_AT
                )
                .from(USERS)
                .orderBy(USERS.ID)
                .limit(safeLimit)
                .offset(safeOffset)
                .fetch(record -> new UserProjection(
                        record.get(USERS.USER_CODE),
                        record.get(USERS.USERNAME),
                        record.get(USERS.EMAIL),
                        record.get(USERS.STATUS),
                        record.get(USERS.CREATED_AT) != null
                                ? record.get(USERS.CREATED_AT).toString()
                                : null,
                        record.get(USERS.LAST_LOGIN_AT) != null
                                ? record.get(USERS.LAST_LOGIN_AT).toString()
                                : null
                ));
    }

    /**
     * Simple projection used by upper layers.
     */

    private int sanitizeLimit(Integer limit) {
        if (limit == null) return DEFAULT_LIMIT;
        return Math.min(Math.max(limit, 1), MAX_LIMIT);
    }

    private int sanitizeOffset(Integer offset) {
        if (offset == null) return 0;
        return Math.max(offset, 0);
    }

}
