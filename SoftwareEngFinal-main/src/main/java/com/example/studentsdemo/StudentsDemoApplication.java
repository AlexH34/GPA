package com.example.studentsdemo;

// Imports for initialization data setup (if uncommented)
/*import com.example.studentsdemo.entities.Student;
import com.example.studentsdemo.repositories.StudentRepository;
import com.example.studentsdemo.web.StudentController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;*/

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentsDemoApplication.class, args);
    }


    /*
    // Example CommandLineRunner to initialize data on startup.
    @Bean
    CommandLineRunner init(StudentRepository repository, StudentController studentController) {

        return args -> {
            // Initial data setup for two students
            // John (ID 102): Course 1 (2 units, A) + Course 2 (3 units, C)
            Student s1 = repository.save(new Student(102, "John","CCSIS 12",2, "A",0.0));
            Student s2 = repository.save(new Student(102, "John", "MATH 101",3,"C" ,0.0));
            Student s3 = repository.save(new Student(103, "Don","CSCI 200",4, "A",0.0));

            // Calculate and save GPA for initial students
            studentController.calculateAndSaveGpaForStudent(s1);
            studentController.calculateAndSaveGpaForStudent(s3);

            repository.findAll().forEach(System.out::println);
        };
    }
    */
}