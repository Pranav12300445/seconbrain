package com.pranav.second_brain_backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean favorite;
}
