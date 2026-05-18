package com.example.marksportal.dto;

import java.util.List;

public record StudentMarksResponse(
        StudentResponse student,
        List<MarkResponse> marks,
        double average
) {
}
