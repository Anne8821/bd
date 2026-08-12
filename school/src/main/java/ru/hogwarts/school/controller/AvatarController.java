package ru.hogwarts.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService service;

    public AvatarController(AvatarService service) {
        this.service = service;
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long uploadAvatar(@PathVariable Long studentId,
                             @RequestParam MultipartFile avatar) throws IOException {

        service.uploadAvatar(studentId, avatar);
        return studentId;
    }

    @GetMapping("/from-db/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {

        Avatar avatar = service.getAvatar(studentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .contentLength(avatar.getFileSize())
                .body(avatar.getData());
    }

    @GetMapping("/from-file/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId)
            throws IOException {

        Avatar avatar = service.getAvatar(studentId);

        Path path = Path.of(avatar.getFilePath());

        byte[] bytes = Files.readAllBytes(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .contentLength(avatar.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(bytes);
    }

    @GetMapping
    public Page<Avatar> getAllAvatars(
            @RequestParam int page,
            @RequestParam int size) {

        return service.getAllAvatars(page, size);
    }
}