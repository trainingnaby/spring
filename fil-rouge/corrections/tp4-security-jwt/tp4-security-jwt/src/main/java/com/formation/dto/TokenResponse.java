package com.formation.dto;

public record TokenResponse(String tokenType, String accessToken, long expiresIn) {
}
