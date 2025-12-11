package com.example.studentsdemo.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Student {
    @Id
    private Integer id;
    private Integer studentNumber;
    private String name;
    private String courseName;
    private Integer units;
    private String Grades;
    private Double Gpa;


}
