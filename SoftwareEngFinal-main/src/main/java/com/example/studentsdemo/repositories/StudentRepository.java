package com.example.studentsdemo.repositories;

import com.example.studentsdemo.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Student entities.
 */
public interface StudentRepository extends JpaRepository<Student, Integer> {

    /**
     * Finds student course records by their unique ID (primary key).
     *
     * @param id The ID of the student course record.
     * @return A list containing the matching student record (will contain at most one element).
     */
    List<Student> findStudentById(int id);

    /**
     * Finds all course records associated with a specific student number.
     * This is used for GPA calculation and name consistency checks.
     *
     * @param studentNumber The unique student number.
     * @return A list of all course records for that student number.
     */
    List<Student> findByStudentNumber(Integer studentNumber);
}