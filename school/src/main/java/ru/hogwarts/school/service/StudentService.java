package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student add(Student student) {
        return repository.save(student);
    }

    public Student get(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Student> getAll() {
        return repository.findAll();
    }

    public Student update(Student student) {
        return repository.save(student);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Faculty getFaculty(Long studentId) {
        Student student = repository.findById(studentId).orElse(null);

        if (student == null) {
            return null;
        }

        return student.getFaculty();
    }

    public List<Student> findByAgeBetween(int min, int max) {
        return repository.findByAgeBetween(min, max);
    }

    public long getStudentsCount() {
        return repository.countStudents();
    }

    public Double getAverageAge() {
        Double averageAge = repository.findAverageAge();

        if (averageAge == null) {
            return 0.0;
        }

        return averageAge;
    }

    public List<Student> getLastFiveStudents() {
        return repository.findLastFiveStudents();
    }
}