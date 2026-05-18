package com.example.marksportal.dto;

import com.example.marksportal.model.Role;

public record AuthResponse(
        String token,
        Role role,
        String username
) {
}
