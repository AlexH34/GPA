package com.example.studentsdemo;

import com.example.studentsdemo.entities.Student;
import com.example.studentsdemo.repositories.StudentRepository;
import com.example.studentsdemo.web.StudentController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class StudentsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentsDemoApplication.class, args);
    }


    /*@Bean
    CommandLineRunner init(StudentRepository repository, StudentController studentController) {

        return args -> {
            // FIX: Initial data setup modified to use different grades for Student 102
            // John: Course 1 (2 units, A) + Course 2 (3 units, C) -> GPA should be 2.80
            Student s1 = repository.save(new Student(102, "John","CCSIS 12",2, "A",0.0));
            Student s2 = repository.save(new Student(102, "John", "MATH 101",3,"C" ,0.0)); // Student 102, different course, different grade
            Student s3 = repository.save(new Student(103, "Don","CSCI 200",4, "A",0.0));

            // Calculate GPA for initial students after saving
            studentController.calculateAndSaveGpaForStudent(s1);
            studentController.calculateAndSaveGpaForStudent(s3);

            repository.findAll().forEach(System.out::println);
        };


    }*/


}