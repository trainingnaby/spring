package com.formation.web.auth;

import java.util.List;

public record LoginResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        String username,
        List<String> roles) {
}
