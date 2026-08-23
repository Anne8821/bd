package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import ru.hogwarts.school.model.Faculty;
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
        assertNotNull(response.getBody());
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
        Student student = new Student("Student For Get All", 21);

        ResponseEntity<Student> created = restTemplate.postForEntity(
                url("/student"),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().getId());

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                url("/student"),
                Student[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);

        assertTrue(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(s ->
                                "Student For Get All".equals(s.getName())
                                        && s.getAge() == 21
                        )
        );
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
        assertNotNull(created.getBody());

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                url("/student/age?min=10&max=20"),
                Student[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertTrue(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(s ->
                                "Student For Age".equals(s.getName())
                                        && s.getAge() == 15
                        )
        );
    }

    @Test
    void getStudentFacultyTest() {
        Faculty faculty = new Faculty("Student Test Faculty", "Red");

        ResponseEntity<Faculty> createdFaculty = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, createdFaculty.getStatusCode());
        assertNotNull(createdFaculty.getBody());
        assertNotNull(createdFaculty.getBody().getId());

        Student student = new Student("Student With Faculty", 19);

        student.setFaculty(createdFaculty.getBody());

        ResponseEntity<Student> createdStudent = restTemplate.postForEntity(
                url("/student"),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, createdStudent.getStatusCode());
        assertNotNull(createdStudent.getBody());
        assertNotNull(createdStudent.getBody().getId());

        Long studentId = createdStudent.getBody().getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                url("/student/" + studentId + "/faculty"),
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdFaculty.getBody().getId(), response.getBody().getId());
        assertEquals("Student Test Faculty", response.getBody().getName());
        assertEquals("Red", response.getBody().getColor());
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