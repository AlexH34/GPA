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

    // Redirects the root path to /index
    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index";
    }

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
        // Find the student to delete to get the studentNumber for GPA update
        Student deletedStudent = studentRepository.findById(id).orElse(null);

        studentRepository.deleteById(id);

        // Re-calculate GPA for the student after deletion
        if (deletedStudent != null) {
            // Create a transient Student object for the GPA calculation logic to use the studentNumber
            Student dummyStudent = new Student();
            dummyStudent.setStudentNumber(deletedStudent.getStudentNumber());
            calculateAndSaveGpaForStudent(dummyStudent);
        }

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
            // This save operation should now succeed because the ID will be generated
            studentRepository.save(student);

            // Call the GPA calculation and update method after saving
            calculateAndSaveGpaForStudent(student);

            if (num == 2) {
                mm.put("e", 2);
                mm.put("a", 0);
            } else {
                mm.put("a", 1);
                mm.put("e", 0);
            }
            return "redirect:/index"; // Correct redirection
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

    // FIX: Helper method to convert grade string to point value, updated with new scores
    private double getGradePointValue(String grade) {
        if (grade == null) return 0.0;
        return switch (grade.toUpperCase()) {
            case "A+" -> 4.33;
            case "A" -> 4.00;
            case "A-" -> 3.67;
            case "B+" -> 3.33;
            case "B" -> 3.00;
            case "B-" -> 2.67;
            case "C+" -> 2.33;
            case "C" -> 2.00;
            case "C-" -> 1.67;
            case "P" -> 1.00;
            case "F" -> 0.00;
            default -> 0.0;
        };
    }

    // Calculates the weighted average GPA for the student and persists it
    public void calculateAndSaveGpaForStudent(Student student) {

        if (student == null || student.getStudentNumber() == null) {
            return;
        }

        List<Student> allCourses = studentRepository.findAll();

        double totalGradePoints = 0.0;
        int totalUnits = 0;
        Integer targetStudentNumber = student.getStudentNumber();

        // 1. Calculate total grade points and total units
        for (Student s : allCourses) {
            // Find all records for the student
            if (s.getStudentNumber() != null && s.getStudentNumber().equals(targetStudentNumber)) {

                // Calculate grade points for this course record: (Grade Point * Units)
                double gradePoint = getGradePointValue(s.getGrades());
                int units = s.getUnits() != null ? s.getUnits() : 0;

                totalGradePoints += gradePoint * units;
                totalUnits += units;
            }
        }

        double gpa = (totalUnits > 0) ? totalGradePoints / totalUnits : 0.0;

        // 2. Apply the newly calculated GPA to ALL of the student's records and save
        for (Student s : allCourses) {
            if (s.getStudentNumber() != null && s.getStudentNumber().equals(targetStudentNumber)) {
                s.setGpa(gpa);
                // Must save to persist the new GPA value
                studentRepository.save(s);
            }
        }
    }

}