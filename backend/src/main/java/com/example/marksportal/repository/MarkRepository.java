package com.example.marksportal.repository;

import com.example.marksportal.model.Mark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudentRollNumberOrderBySubjectAsc(String rollNumber);
}
