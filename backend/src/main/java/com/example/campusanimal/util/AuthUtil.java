package com.example.campusanimal.util;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static String extractBearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return "";
        }
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }
        return authorization.trim();
    }
}
