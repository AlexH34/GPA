package com.example.studentsdemo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a student's course record and associated GPA.
 * Each record represents a single course taken by a student.
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer studentNumber;
    private String name;
    private String courseName;
    private Integer units;
    private String grades;
    private Double gpa; // Overall GPA for the student

    /**
     * Constructor for creating a new student course record.
     * The 'id' is generated automatically by the database.
     */
    public Student(Integer studentNumber, String name, String courseName, Integer units, String grades, Double gpa) {
        this.studentNumber = studentNumber;
        this.name = name;
        this.courseName = courseName;
        this.units = units;
        this.grades = grades;
        this.gpa = gpa;
    }
}