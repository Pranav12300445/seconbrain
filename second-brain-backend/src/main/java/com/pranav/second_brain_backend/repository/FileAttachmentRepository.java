package com.pranav.second_brain_backend.repository;

import com.pranav.second_brain_backend.model.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {

    List<FileAttachment> findByUserEmailOrderByUploadedAtDesc(String email);

    List<FileAttachment> findByNoteIdAndUserEmail(Long noteId, String email);

    Optional<FileAttachment> findByIdAndUserEmail(Long id, String email);
}