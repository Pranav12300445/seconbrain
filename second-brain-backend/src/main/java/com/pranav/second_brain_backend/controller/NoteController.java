package com.pranav.second_brain_backend.controller;

import com.pranav.second_brain_backend.dto.NoteRequest;
import com.pranav.second_brain_backend.dto.NoteResponse;
import com.pranav.second_brain_backend.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notes")
@CrossOrigin
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse createNote(@Valid @RequestBody NoteRequest request,
            Authentication authentication) {
        return noteService.createNote(request, authentication.getName());
    }

    @GetMapping
    public List<NoteResponse> getAllNotes(Authentication authentication) {
        return noteService.getAllNotes(authentication.getName());
    }

    @PutMapping("/{noteId}")
    public NoteResponse updateNote(@PathVariable Long noteId,
            @Valid @RequestBody NoteRequest request,
            Authentication authentication) {
        return noteService.updateNote(noteId, request, authentication.getName());
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long noteId,
            Authentication authentication) {
        noteService.deleteNote(noteId, authentication.getName());
    }
}
