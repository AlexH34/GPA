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
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Controller for handling student course records and GPA calculations.
 */
@SessionAttributes({"a", "e"})
@Controller
@AllArgsConstructor
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    static  int num =0;

    /**
     * Redirects the root path to /index.
     */
    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index";
    }

    /**
     * Handles the student list view, supporting search by student ID.
     * It prepares two lists:
     * 1. A detailed list of all course records (listStudents).
     * 2. A summary list of unique students with their overall GPA (listUniqueStudents).
     *
     * @param model The Spring UI model.
     * @param keyword Optional keyword for searching by student ID.
     * @return The "students" view template.
     */
    @GetMapping(path = "index")
    private String students(Model model, @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        List<Student> allStudents;
        if (keyword.isEmpty()) {
            allStudents = studentRepository.findAll();
        } else {
            // Search logic assumes keyword is an integer ID
            int key = Integer.parseInt(keyword);
            allStudents = studentRepository.findStudentById(key);
        }

        // Create a list of unique students for the GPA summary table.
        // Group by studentNumber, keeping the first record encountered to represent the student.
        List<Student> uniqueStudents = allStudents.stream()
                .collect(Collectors.toMap(
                        Student::getStudentNumber,
                        student -> student,
                        (existing, replacement) -> existing // Keep the existing student record
                ))
                .values().stream()
                .sorted(Comparator.comparing(Student::getStudentNumber))
                .collect(Collectors.toList());

        model.addAttribute("listUniqueStudents", uniqueStudents);
        // Add the original list of course records (detailed list)
        model.addAttribute("listStudents", allStudents);
        return "students";
    }


    /**
     * Deletes a student course record by its ID and recalculates the student's GPA.
     *
     * @param id The ID of the course record to delete.
     * @param model The Spring UI model (unused but kept for method signature context).
     * @return Redirects to the index page.
     */
    @GetMapping("/delete")
    public String deleteStudent(@RequestParam(name = "id", required = true) int id, Model model) {
        // Find the student record before deletion to get the studentNumber for GPA update
        Student deletedStudent = studentRepository.findById(id).orElse(null);

        studentRepository.deleteById(id);

        // Re-calculate GPA for the student after a course record deletion
        if (deletedStudent != null) {
            // Create a temporary Student object to hold the studentNumber for GPA recalculation
            Student dummyStudent = new Student();
            dummyStudent.setStudentNumber(deletedStudent.getStudentNumber());
            calculateAndSaveGpaForStudent(dummyStudent);
        }

        return "redirect:/index";
    }

    /**
     * Displays the form for adding a new student course record.
     *
     * @param model The Spring UI model.
     * @return The "formStudents" view template.
     */
    @GetMapping("/formStudents")
    public String formStudents(Model model) {
        model.addAttribute("student", new Student());
        return "formStudents";
    }


    /**
     * Handles saving a new or updated student course record.
     * It includes validation to ensure the student name is consistent across all records for a given student number.
     *
     * @param model The Spring UI model.
     * @param student The Student object populated from the form.
     * @param bindingResult Validation results (not fully utilized for complex validation here).
     * @param mm ModelMap for session attributes (a, e).
     * @param session HttpSession (not fully utilized).
     * @return Redirects to the index page or returns to the form if validation fails.
     */
    @PostMapping(path = "/save")
    public String save(Model model, Student student, BindingResult
            bindingResult, ModelMap mm, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "formStudents";
        }

        // Validate name consistency: A studentNumber must always have the same student name.
        List<Student> existingStudents = studentRepository.findByStudentNumber(student.getStudentNumber());

        String submittedName = student.getName();
        Integer submittedStudentNumber = student.getStudentNumber();
        boolean isNameMismatch = false;

        // Check 1: If any records with this studentNumber already exist
        if (!existingStudents.isEmpty()) {
            String existingName = existingStudents.get(0).getName();

            // Check for name conflict
            if (!existingName.equals(submittedName)) {
                isNameMismatch = true;
            }
        }

        if (isNameMismatch) {
            model.addAttribute("errorMessage", "Student Number " + submittedStudentNumber + " is already associated with the name '" + existingStudents.get(0).getName() + "'. The name must be consistent across all records.");
            // If editing, return to editStudents form
            if (student.getId() != null) {
                return "editStudents";
            }
            // If adding, return to formStudents form
            return "formStudents";
        }

        // Proceed with saving if validation passes
        studentRepository.save(student);

        // Calculate and update GPA for the student after saving the new course record
        calculateAndSaveGpaForStudent(student);

        if (num == 2) {
            mm.put("e", 2);
            mm.put("a", 0);
        } else {
            mm.put("a", 1);
            mm.put("e", 0);
        }
        return "redirect:/index";
    }


    /**
     * Displays the form for editing an existing student course record.
     *
     * @param model The Spring UI model.
     * @param id The ID of the course record to edit.
     * @param session HttpSession (used for temporary state `num` and `info`).
     * @return The "editStudents" view template.
     */
    @GetMapping("/editStudents")
    public String editStudents(Model model, int id, HttpSession session) {
        num = 2;
        session.setAttribute("info", 0);
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) throw new RuntimeException("Student does not exist");
        model.addAttribute("student", student);
        return "editStudents";
    }

    /**
     * Converts a letter grade string to its corresponding GPA point value.
     *
     * @param grade The letter grade (e.g., "A+", "B", "F").
     * @return The GPA point value (e.g., 4.33, 3.00, 0.00). Returns 0.0 if grade is null or not recognized.
     */
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

    /**
     * Calculates the weighted average GPA for a specific student and updates all of their course records in the database.
     * It fetches only the records for the target student number, which improves performance.
     *
     * @param student A Student object containing the studentNumber for which to calculate the GPA.
     */
    public void calculateAndSaveGpaForStudent(Student student) {

        if (student == null || student.getStudentNumber() == null) {
            return;
        }

        // Fetch only the courses for the target student
        List<Student> allCourses = studentRepository.findByStudentNumber(student.getStudentNumber());

        double totalGradePoints = 0.0;
        int totalUnits = 0;

        // 1. Calculate total grade points and total units
        for (Student s : allCourses) {
            // Calculate grade points for this course record: (Grade Point * Units)
            double gradePoint = getGradePointValue(s.getGrades());
            int units = s.getUnits() != null ? s.getUnits() : 0;

            totalGradePoints += gradePoint * units;
            totalUnits += units;
        }

        double gpa = (totalUnits > 0) ? totalGradePoints / totalUnits : 0.0;

        // 2. Apply the newly calculated GPA to ALL of the student's records and save
        // We iterate over the already filtered list `allCourses`
        for (Student s : allCourses) {
            s.setGpa(gpa);
            // Save to persist the new GPA value
            studentRepository.save(s);
        }
    }
}