package com.example.marksportal.controller;

import com.example.marksportal.dto.MarkRequest;
import com.example.marksportal.dto.MarkResponse;
import com.example.marksportal.dto.StudentMarksResponse;
import com.example.marksportal.dto.StudentRequest;
import com.example.marksportal.dto.StudentResponse;
import com.example.marksportal.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final StudentService studentService;

    public TeacherController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public Page<StudentResponse> students(@PageableDefault(size = 5, sort = "rollNumber") Pageable pageable) {
        return studentService.findAll(pageable);
    }

    @PostMapping("/students")
    public StudentResponse createStudent(@Valid @RequestBody StudentRequest request) {
        return studentService.create(request);
    }

    @GetMapping("/students/{id}")
    public StudentResponse student(@PathVariable Long id) {
        return studentService.findById(id);
    }

    @PutMapping("/students/{id}")
    public StudentResponse updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return studentService.update(id, request);
    }

    @DeleteMapping("/students/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
    }

    @GetMapping("/students/{rollNumber}/marks")
    public StudentMarksResponse studentMarks(@PathVariable String rollNumber) {
        return studentService.findMarksByRollNumber(rollNumber);
    }

    @PostMapping("/students/{studentId}/marks")
    public MarkResponse addMark(@PathVariable Long studentId, @Valid @RequestBody MarkRequest request) {
        return studentService.addMark(studentId, request);
    }

    @PutMapping("/marks/{markId}")
    public MarkResponse updateMark(@PathVariable Long markId, @Valid @RequestBody MarkRequest request) {
        return studentService.updateMark(markId, request);
    }

    @DeleteMapping("/marks/{markId}")
    public void deleteMark(@PathVariable Long markId) {
        studentService.deleteMark(markId);
    }
}
