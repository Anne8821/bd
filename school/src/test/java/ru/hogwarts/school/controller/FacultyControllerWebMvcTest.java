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
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService service;

    @Test
    void addFacultyTest() throws Exception {
        Faculty faculty = new Faculty("Test Faculty", "Red");
        faculty.setId(1L);

        when(service.add(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Faculty"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void getFacultyTest() throws Exception {
        Faculty faculty = new Faculty("Faculty For Get", "Blue");
        faculty.setId(1L);

        when(service.get(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Faculty For Get"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }

    @Test
    void getAllFacultiesTest() throws Exception {
        Faculty faculty = new Faculty("Faculty One", "Green");
        faculty.setId(1L);

        when(service.getAll()).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Faculty One"))
                .andExpect(jsonPath("$[0].color").value("Green"));
    }

    @Test
    void updateFacultyTest() throws Exception {
        Faculty faculty = new Faculty("Updated Faculty", "Yellow");
        faculty.setId(1L);

        when(service.update(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Faculty"))
                .andExpect(jsonPath("$.color").value("Yellow"));
    }

    @Test
    void deleteFacultyTest() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/faculty/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findFacultiesTest() throws Exception {
        Faculty faculty = new Faculty("Faculty For Search", "Purple");
        faculty.setId(1L);

        when(service.findByNameOrColor("Purple"))
                .thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty/search")
                        .param("value", "Purple"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Faculty For Search"))
                .andExpect(jsonPath("$[0].color").value("Purple"));
    }

    @Test
    void getFacultyStudentsTest() throws Exception {
        Student student = new Student("Student One", 20);
        student.setId(1L);

        when(service.getStudents(1L))
                .thenReturn(List.of(student));

        mockMvc.perform(get("/faculty/{id}/students", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Student One"))
                .andExpect(jsonPath("$[0].age").value(20));
    }

    @Test
    void getFacultyNotFoundTest() throws Exception {
        when(service.get(999999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/{id}", 999999L))
                .andExpect(status().isNotFound());
    }
}