package com.example.marksportal.controller;

import com.example.marksportal.dto.StudentMarksResponse;
import com.example.marksportal.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/me/marks")
    public StudentMarksResponse myMarks(Authentication authentication) {
        return studentService.findMarksByRollNumber(authentication.getName());
    }
}
