package com.example.studentsdemo;

import com.example.studentsdemo.entities.Student;
import com.example.studentsdemo.repositories.StudentRepository;
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


    @Bean
    CommandLineRunner init(StudentRepository repository) {

        return args -> {
            repository.save(new Student(101,102, "John","CCSIS 12",2, "A",0.0));
            repository.save(new Student(102, 102,"Jack", "CCSIS 12",3,"A" ,0.0));
            repository.save(new Student(103, 103,"Don","CCSIS 12",4, "A",0.0));

            repository.findAll().forEach(System.out::println);
        };


    }


}
