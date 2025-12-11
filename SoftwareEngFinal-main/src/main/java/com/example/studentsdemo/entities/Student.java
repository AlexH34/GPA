package com.example.studentsdemo.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
// Removed @AllArgsConstructor to fix conflict with @GeneratedValue in CommandLineRunner
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
    private Double gpa;

    // Manual constructor for creating initial records without specifying the generated ID
    public Student(Integer studentNumber, String name, String courseName, Integer units, String grades, Double gpa) {
        this.studentNumber = studentNumber;
        this.name = name;
        this.courseName = courseName;
        this.units = units;
        this.grades = grades;
        this.gpa = gpa;
    }
}