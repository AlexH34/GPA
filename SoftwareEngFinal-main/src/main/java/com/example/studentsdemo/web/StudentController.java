package com.example.studentsdemo.web;

import com.example.studentsdemo.entities.Student;
import com.example.studentsdemo.repositories.StudentRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SessionAttributes({"a", "e"})
@Controller
@AllArgsConstructor

public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
static  int num =0;
    @GetMapping(path = "index")
    private String students(Model model, @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        List<Student> students;
        if (keyword.isEmpty()) {
            students = studentRepository.findAll();

        } else {
            int key = Integer.parseInt(keyword);
            students = studentRepository.findStudentById(key);

        }
        model.addAttribute("listStudents", students);
        return "students";
    }


    @GetMapping("/delete")
    public String deleteStudent(@RequestParam(name = "id", required = true) int id, Model model) {
        studentRepository.deleteById(id);
        return "redirect:/index";
    }

    @GetMapping("/formStudents")
    public String formStudents(Model model) {

        model.addAttribute("student", new Student());
        return "formStudents";
    }


    @PostMapping(path = "/save")
    public String save(Model model, Student student, BindingResult
            bindingResult, ModelMap mm, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "formStudents";
        } else {
            studentRepository.save(student);
            if (num == 2) {
                mm.put("e", 2);
                mm.put("a", 0);
            } else {
                mm.put("a", 1);
                mm.put("e", 0);
            }
            return "redirect:index";
        }

    }


    @GetMapping("/editStudents")
    public String editStudents(Model model, int id, HttpSession session) {
        num = 2;
        session.setAttribute("info", 0);
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) throw new RuntimeException("Student does not exist");
        model.addAttribute("student", student);
        return "editStudents";
    }


    public Double gpaCalculator(Student student) {
        List<Student> students;
        students = studentRepository.findAll();
        double gpa = 0;
        int credits=3;
        for (Student s : students) {
            if (s.getStudentNumber() == student.getStudentNumber()) {
                if (s.getGrades() =="A") {
                    gpa=gpa+4.00;

                }
                if (s.getGrades() =="B") {
                    gpa=gpa+3.00;

                }
                if (s.getGrades() =="C") {
                    gpa=gpa+2.00;

                }
                if (s.getGrades() =="D") {
                    gpa=gpa+1.00;

                }
                if (s.getGrades() =="F") {
                    gpa=gpa+0.00;

                }

            }
            s.setGpa(gpa/credits);
        }
        return gpa;

    }

}