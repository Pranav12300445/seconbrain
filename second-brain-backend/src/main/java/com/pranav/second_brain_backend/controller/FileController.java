package com.pranav.second_brain_backend.controller;

import com.pranav.second_brain_backend.dto.FileAttachmentResponse;
import com.pranav.second_brain_backend.service.FileAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@CrossOrigin
@RequiredArgsConstructor
public class FileController {

    private final FileAttachmentService fileAttachmentService;

    /**
     * POST /files/upload
     * Optional query param: noteId — links the file to a specific note
     */
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public FileAttachmentResponse uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long noteId,
            Authentication auth) throws IOException {

        return fileAttachmentService.uploadFile(file, noteId, auth.getName());
    }

    /**
     * GET /files — all files for the logged-in user
     */
    @GetMapping
    public List<FileAttachmentResponse> getAllFiles(Authentication auth) {
        return fileAttachmentService.getAllFiles(auth.getName());
    }

    /**
     * GET /files/note/{noteId} — files attached to a specific note
     */
    @GetMapping("/note/{noteId}")
    public List<FileAttachmentResponse> getFilesForNote(
            @PathVariable Long noteId,
            Authentication auth) {
        return fileAttachmentService.getFilesForNote(noteId, auth.getName());
    }

    /**
     * DELETE /files/{fileId}
     */
    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@PathVariable Long fileId, Authentication auth) {
        fileAttachmentService.deleteFile(fileId, auth.getName());
    }
}