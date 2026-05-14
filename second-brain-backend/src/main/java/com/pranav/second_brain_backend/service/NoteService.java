package com.pranav.second_brain_backend.service;

import com.pranav.second_brain_backend.dto.NoteRequest;
import com.pranav.second_brain_backend.dto.NoteResponse;
import com.pranav.second_brain_backend.exception.ResourceNotFoundException;
import com.pranav.second_brain_backend.model.Note;
import com.pranav.second_brain_backend.model.User;
import com.pranav.second_brain_backend.repository.NoteRepository;
import com.pranav.second_brain_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoteResponse createNote(NoteRequest request, String email) {
        User user = getUserByEmail(email);

        Note note = Note.builder()
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .favorite(Boolean.TRUE.equals(request.getFavorite()))
                .user(user)
                .build();

        return toResponse(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAllNotes(String email) {
        return noteRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NoteResponse updateNote(Long noteId, NoteRequest request, String email) {
        Note note = getOwnedNote(noteId, email);

        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent().trim());

        if (request.getFavorite() != null) {
            note.setFavorite(request.getFavorite());
        }

        return toResponse(note);
    }

    @Transactional
    public void deleteNote(Long noteId, String email) {
        Note note = getOwnedNote(noteId, email);
        noteRepository.delete(note);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private Note getOwnedNote(Long noteId, String email) {
        return noteRepository.findByIdAndUserEmail(noteId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
    }

    private NoteResponse toResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .favorite(note.isFavorite())
                .build();
    }
}
