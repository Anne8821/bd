package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService service;

    @Test
    void addStudentTest() throws Exception {
        Student student = new Student("Test Student", 20);
        student.setId(1L);

        when(service.add(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Student"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    void getStudentTest() throws Exception {
        Student student = new Student("Student For Get", 18);
        student.setId(1L);

        when(service.get(1L)).thenReturn(student);

        mockMvc.perform(get("/student/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Student For Get"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void getAllStudentsTest() throws Exception {
        Student student = new Student("Student One", 20);
        student.setId(1L);

        when(service.getAll()).thenReturn(List.of(student));

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Student One"))
                .andExpect(jsonPath("$[0].age").value(20));
    }

    @Test
    void updateStudentTest() throws Exception {
        Student student = new Student("Updated Student", 25);
        student.setId(1L);

        when(service.update(any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Student"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void deleteStudentTest() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/student/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findStudentsByAgeTest() throws Exception {
        Student student = new Student("Student For Age", 15);
        student.setId(1L);

        when(service.findByAgeBetween(10, 20))
                .thenReturn(List.of(student));

        mockMvc.perform(get("/student/age")
                        .param("min", "10")
                        .param("max", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Student For Age"))
                .andExpect(jsonPath("$[0].age").value(15));
    }

    @Test
    void getStudentFacultyTest() throws Exception {
        Faculty faculty = new Faculty("Test Faculty", "Red");
        faculty.setId(1L);

        when(service.getFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/{id}/faculty", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Faculty"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void getStudentNotFoundTest() throws Exception {
        when(service.get(999999L)).thenReturn(null);

        mockMvc.perform(get("/student/{id}", 999999L))
                .andExpect(status().isNotFound());
    }
}