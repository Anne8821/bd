package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return service.add(student);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        Student student = service.get(id);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return service.getAll();
    }

    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        return service.update(student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id) {
        Faculty faculty = service.getFaculty(id);

        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/age")
    public List<Student> findStudentsByAge(
            @RequestParam int min,
            @RequestParam int max) {

        return service.findByAgeBetween(min, max);
    }

    @GetMapping("/count")
    public long getStudentsCount() {
        return service.getStudentsCount();
    }

    @GetMapping("/average-age")
    public Double getAverageAge() {
        return service.getAverageAge();
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return service.getLastFiveStudents();
    }
}