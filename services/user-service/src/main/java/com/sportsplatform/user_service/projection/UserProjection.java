package com.sportsplatform.user_service.projection;


public record UserProjection(
        String userCode,
        String username,
        String email,
        String status,
        String createdAt,
        String lastLoginAt
) {}
