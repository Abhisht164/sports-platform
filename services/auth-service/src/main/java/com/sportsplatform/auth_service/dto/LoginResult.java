package com.sportsplatform.auth_service.dto;

import java.util.List;
import java.util.UUID;

public record LoginResult(
        UUID userId,
        String username,
        List<String> roles
) {}