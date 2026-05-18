package com.example.marksportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StudentRequest(
        @NotBlank String name,
        @NotBlank String rollNumber,
        @Email String email,
        String className,
        String password
) {
}
