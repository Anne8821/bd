package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void addStudentTest() {
        Student student = new Student("Test Student", 20);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                url("/student"),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("Test Student", response.getBody().getName());
        assertEquals(20, response.getBody().getAge());
    }

    @Test
    void getStudentTest() {
        Student student = new Student("Student For Get", 18);

        ResponseEntity<Student> created = restTemplate.postForEntity(
                url("/student"),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());

        Long id = created.getBody().getId();

        ResponseEntity<Student> response = restTemplate.getForEntity(
                url("/student/" + id),
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Student For Get", response.getBody().getName());
        assertEquals(18, response.getBody().getAge());
    }

    @Test
    void getAllStudentsTest() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                url("/student"),
                Student[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updateStudentTest() {
        Student student = new Student("Student Before Update", 20);

        ResponseEntity<Student> created = restTemplate.postForEntity(
                url("/student"),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());

        Long id = created.getBody().getId();

        Student updatedStudent = new Student("Updated Student", 25);
        updatedStudent.setId(id);

        HttpEntity<Student> request = new HttpEntity<>(updatedStudent);

        ResponseEntity<Student> response = restTemplate.exchange(
                url("/student"),
                HttpMethod.PUT,
                request,
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Updated Student", response.getBody().getName());
        assertEquals(25, response.getBody().getAge());
    }

    @Test
    void deleteStudentTest() {
        Student student = new Student("Student For Delete", 22);

        ResponseEntity<Student> created = restTemplate.postForEntity(
                url("/student"),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());

        Long id = created.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/student/" + id),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                url("/student/" + id),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void findStudentsByAgeTest() {
        Student student = new Student("Student For Age", 15);

        ResponseEntity<Student> created = restTemplate.postForEntity(
                url("/student"),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                url("/student/age?min=10&max=20"),
                Student[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertTrue(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(s -> "Student For Age".equals(s.getName()))
        );
    }

    @Test
    void getStudentNotFoundTest() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/student/999999"),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}