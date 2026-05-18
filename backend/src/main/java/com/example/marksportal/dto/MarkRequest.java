package com.example.marksportal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MarkRequest(
        @NotBlank String subject,
        @NotNull @Min(0) @Max(100) Integer score
) {
}
