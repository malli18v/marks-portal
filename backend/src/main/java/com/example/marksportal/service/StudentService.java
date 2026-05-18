package com.example.marksportal.service;

import com.example.marksportal.dto.MarkRequest;
import com.example.marksportal.dto.MarkResponse;
import com.example.marksportal.dto.StudentMarksResponse;
import com.example.marksportal.dto.StudentRequest;
import com.example.marksportal.dto.StudentResponse;
import com.example.marksportal.model.AppUser;
import com.example.marksportal.model.Mark;
import com.example.marksportal.model.Role;
import com.example.marksportal.model.Student;
import com.example.marksportal.repository.MarkRepository;
import com.example.marksportal.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final MarkRepository markRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
            StudentRepository studentRepository,
            MarkRepository markRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.studentRepository = studentRepository;
        this.markRepository = markRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<StudentResponse> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable).map(this::toStudentResponse);
    }

    public StudentResponse findById(Long id) {
        return toStudentResponse(getStudent(id));
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByRollNumber(request.rollNumber())) {
            throw new IllegalArgumentException("Roll number already exists");
        }
        String rawPassword = request.password() == null || request.password().isBlank()
                ? request.rollNumber()
                : request.password();
        AppUser user = new AppUser(request.rollNumber(), passwordEncoder.encode(rawPassword), Role.STUDENT);
        Student student = new Student(request.name(), request.rollNumber(), request.email(), request.className(), user);
        return toStudentResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = getStudent(id);
        student.setName(request.name());
        student.setEmail(request.email());
        student.setClassName(request.className());
        if (!student.getRollNumber().equals(request.rollNumber())) {
            if (studentRepository.existsByRollNumber(request.rollNumber())) {
                throw new IllegalArgumentException("Roll number already exists");
            }
            student.setRollNumber(request.rollNumber());
            student.getUser().setUsername(request.rollNumber());
        }
        if (request.password() != null && !request.password().isBlank()) {
            student.getUser().setPassword(passwordEncoder.encode(request.password()));
        }
        return toStudentResponse(student);
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.delete(getStudent(id));
    }

    public StudentMarksResponse findMarksByRollNumber(String rollNumber) {
        Student student = studentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        List<MarkResponse> marks = markRepository.findByStudentRollNumberOrderBySubjectAsc(rollNumber)
                .stream()
                .map(this::toMarkResponse)
                .toList();
        double average = marks.stream().mapToInt(MarkResponse::score).average().orElse(0);
        return new StudentMarksResponse(toStudentResponse(student), marks, average);
    }

    @Transactional
    public MarkResponse addMark(Long studentId, MarkRequest request) {
        Student student = getStudent(studentId);
        Mark mark = new Mark(request.subject(), request.score(), student);
        return toMarkResponse(markRepository.save(mark));
    }

    @Transactional
    public MarkResponse updateMark(Long markId, MarkRequest request) {
        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new EntityNotFoundException("Mark not found"));
        mark.setSubject(request.subject());
        mark.setScore(request.score());
        return toMarkResponse(mark);
    }

    @Transactional
    public void deleteMark(Long markId) {
        markRepository.deleteById(markId);
    }

    private Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
    }

    private StudentResponse toStudentResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getRollNumber(),
                student.getEmail(),
                student.getClassName()
        );
    }

    private MarkResponse toMarkResponse(Mark mark) {
        return new MarkResponse(mark.getId(), mark.getSubject(), mark.getScore());
    }
}
