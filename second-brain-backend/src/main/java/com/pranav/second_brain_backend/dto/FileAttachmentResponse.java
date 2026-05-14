package com.pranav.second_brain_backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAttachmentResponse {

    private Long id;
    private String originalName;
    private String publicUrl;
    private String contentType;
    private Long fileSize;
    private Long noteId; // null if not linked to a note
    private LocalDateTime uploadedAt;
}