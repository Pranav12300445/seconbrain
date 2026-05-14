package com.pranav.second_brain_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be 150 characters or less")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Boolean favorite;
}
