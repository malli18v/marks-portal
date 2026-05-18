package com.example.marksportal.dto;

public record StudentResponse(
        Long id,
        String name,
        String rollNumber,
        String email,
        String className
) {
}
