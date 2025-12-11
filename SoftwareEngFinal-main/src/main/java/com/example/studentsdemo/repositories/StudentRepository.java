package com.example.studentsdemo.repositories;

import com.example.studentsdemo.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findStudentById(int kw);

}
