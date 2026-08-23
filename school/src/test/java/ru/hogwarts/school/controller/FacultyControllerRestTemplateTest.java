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
public class FacultyControllerRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void addFacultyTest() {
        Faculty faculty = new Faculty("Test Faculty", "Red");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Test Faculty", response.getBody().getName());
        assertEquals("Red", response.getBody().getColor());
    }

    @Test
    void getFacultyTest() {
        Faculty faculty = new Faculty("Faculty For Get", "Blue");

        ResponseEntity<Faculty> created = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());

        Long id = created.getBody().getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                url("/faculty/" + id),
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Faculty For Get", response.getBody().getName());
        assertEquals("Blue", response.getBody().getColor());
    }

    @Test
    void getAllFacultiesTest() {
        Faculty faculty = new Faculty("Faculty For Get All", "Orange");

        ResponseEntity<Faculty> created = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                url("/faculty"),
                Faculty[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);

        assertTrue(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(f ->
                                "Faculty For Get All".equals(f.getName())
                                        && "Orange".equals(f.getColor())
                        )
        );
    }

    @Test
    void updateFacultyTest() {
        Faculty faculty = new Faculty("Faculty Before Update", "Green");

        ResponseEntity<Faculty> created = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());

        Long id = created.getBody().getId();

        Faculty updatedFaculty = new Faculty("Updated Faculty", "Yellow");
        updatedFaculty.setId(id);

        HttpEntity<Faculty> request = new HttpEntity<>(updatedFaculty);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                url("/faculty"),
                HttpMethod.PUT,
                request,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Updated Faculty", response.getBody().getName());
        assertEquals("Yellow", response.getBody().getColor());
    }

    @Test
    void deleteFacultyTest() {
        Faculty faculty = new Faculty("Faculty For Delete", "Black");

        ResponseEntity<Faculty> created = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());

        Long id = created.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/faculty/" + id),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                url("/faculty/" + id),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void getFacultyStudentsTest() {
        Faculty faculty = new Faculty("Faculty With Students", "Blue");

        ResponseEntity<Faculty> createdFaculty = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, createdFaculty.getStatusCode());
        assertNotNull(createdFaculty.getBody());

        Long facultyId = createdFaculty.getBody().getId();

        Student student = new Student("Student Of Faculty", 20);
        student.setFaculty(createdFaculty.getBody());

        ResponseEntity<Student> createdStudent = restTemplate.postForEntity(
                url("/student"),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, createdStudent.getStatusCode());
        assertNotNull(createdStudent.getBody());

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                url("/faculty/" + facultyId + "/students"),
                Student[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertTrue(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(s ->
                                "Student Of Faculty".equals(s.getName())
                                        && s.getAge() == 20
                        )
        );
    }

    @Test
    void findFacultiesTest() {
        Faculty faculty = new Faculty("Faculty For Search", "Purple");

        ResponseEntity<Faculty> created = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertNotNull(created.getBody());

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                url("/faculty/search?value=Purple"),
                Faculty[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertTrue(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(f ->
                                "Faculty For Search".equals(f.getName())
                                        && "Purple".equals(f.getColor())
                        )
        );
    }

    @Test
    void getFacultyNotFoundTest() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/faculty/999999"),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}