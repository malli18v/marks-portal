package com.example.marksportal.config;

import com.example.marksportal.model.AppUser;
import com.example.marksportal.model.Mark;
import com.example.marksportal.model.Role;
import com.example.marksportal.model.Student;
import com.example.marksportal.repository.MarkRepository;
import com.example.marksportal.repository.StudentRepository;
import com.example.marksportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            StudentRepository studentRepository,
            MarkRepository markRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!userRepository.existsByUsername("teacher")) {
                userRepository.save(new AppUser("teacher", passwordEncoder.encode("teacher123"), Role.TEACHER));
            }

            if (!studentRepository.existsByRollNumber("R001")) {
                Student anika = studentRepository.save(new Student(
                        "Anika Rao",
                        "R001",
                        "anika@example.com",
                        "10-A",
                        new AppUser("R001", passwordEncoder.encode("student123"), Role.STUDENT)
                ));
                markRepository.save(new Mark("Mathematics", 88, anika));
                markRepository.save(new Mark("Science", 91, anika));
                markRepository.save(new Mark("English", 84, anika));
            }

            if (!studentRepository.existsByRollNumber("R002")) {
                Student kabir = studentRepository.save(new Student(
                        "Kabir Mehta",
                        "R002",
                        "kabir@example.com",
                        "10-A",
                        new AppUser("R002", passwordEncoder.encode("student123"), Role.STUDENT)
                ));
                markRepository.save(new Mark("Mathematics", 76, kabir));
                markRepository.save(new Mark("Science", 82, kabir));
            }
        };
    }
}
