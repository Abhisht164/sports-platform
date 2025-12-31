package com.sportsplatform.user_service.projection;

import java.util.UUID;

public record UserProjection(
        String userCode,
        String username,
        String email,
        String status,
        String createdAt,
        String lastLoginAt
) {}
