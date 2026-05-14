package com.pranav.second_brain_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_attachments", indexes = {
        @Index(name = "idx_file_user", columnList = "user_id"),
        @Index(name = "idx_file_note", columnList = "note_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalName; // "report.pdf"

    @Column(nullable = false)
    private String storagePath; // "userId/uuid-report.pdf" inside the bucket

    @Column(nullable = false)
    private String publicUrl; // full public URL from Supabase

    @Column(nullable = false)
    private String contentType; // "application/pdf"

    private Long fileSize; // bytes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // optional — link a file to a specific note
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    void prePersist() {
        if (uploadedAt == null)
            uploadedAt = LocalDateTime.now();
    }
}