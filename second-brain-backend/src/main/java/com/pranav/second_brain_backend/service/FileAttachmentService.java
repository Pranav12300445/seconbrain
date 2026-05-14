package com.pranav.second_brain_backend.service;

import com.pranav.second_brain_backend.dto.FileAttachmentResponse;
import com.pranav.second_brain_backend.exception.BadRequestException;
import com.pranav.second_brain_backend.exception.ResourceNotFoundException;
import com.pranav.second_brain_backend.model.FileAttachment;
import com.pranav.second_brain_backend.model.Note;
import com.pranav.second_brain_backend.model.User;
import com.pranav.second_brain_backend.repository.FileAttachmentRepository;
import com.pranav.second_brain_backend.repository.NoteRepository;
import com.pranav.second_brain_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileAttachmentService {

    // Allowed MIME types — extend this list as needed
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .pptx
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "text/plain", "text/markdown",
            "application/zip");

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024L; // 50 MB

    private final FileAttachmentRepository fileAttachmentRepository;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final SupabaseStorageService storageService;

    @Transactional
    public FileAttachmentResponse uploadFile(MultipartFile file, Long noteId, String email)
            throws IOException {

        // ── Validation ─────────────────────────────────────────────────────
        if (file.isEmpty())
            throw new BadRequestException("File is empty");
        if (file.getSize() > MAX_FILE_SIZE)
            throw new BadRequestException("File exceeds 50 MB limit");

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("File type not allowed: " + contentType);
        }

        // ── Fetch user ──────────────────────────────────────────────────────
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // ── Optionally resolve note ─────────────────────────────────────────
        Note note = null;
        if (noteId != null) {
            note = noteRepository.findByIdAndUserEmail(noteId, email)
                    .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        }

        // ── Upload to Supabase Storage ──────────────────────────────────────
        String publicUrl = storageService.uploadFile(file, user.getId());
        String storagePath = storageService.extractStoragePath(publicUrl);

        // ── Persist metadata ────────────────────────────────────────────────
        FileAttachment attachment = FileAttachment.builder()
                .originalName(file.getOriginalFilename())
                .storagePath(storagePath)
                .publicUrl(publicUrl)
                .contentType(contentType)
                .fileSize(file.getSize())
                .user(user)
                .note(note)
                .build();

        FileAttachment saved = fileAttachmentRepository.save(attachment);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FileAttachmentResponse> getAllFiles(String email) {
        return fileAttachmentRepository
                .findByUserEmailOrderByUploadedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FileAttachmentResponse> getFilesForNote(Long noteId, String email) {
        return fileAttachmentRepository
                .findByNoteIdAndUserEmail(noteId, email)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteFile(Long fileId, String email) {
        FileAttachment attachment = fileAttachmentRepository
                .findByIdAndUserEmail(fileId, email)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        // Delete from Supabase Storage first
        storageService.deleteFile(attachment.getStoragePath());

        // Then remove metadata from DB
        fileAttachmentRepository.delete(attachment);
    }

    private FileAttachmentResponse toResponse(FileAttachment f) {
        return FileAttachmentResponse.builder()
                .id(f.getId())
                .originalName(f.getOriginalName())
                .publicUrl(f.getPublicUrl())
                .contentType(f.getContentType())
                .fileSize(f.getFileSize())
                .noteId(f.getNote() != null ? f.getNote().getId() : null)
                .uploadedAt(f.getUploadedAt())
                .build();
    }
}