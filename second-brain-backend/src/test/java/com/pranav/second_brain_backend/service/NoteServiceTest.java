package com.pranav.second_brain_backend.service;

import com.pranav.second_brain_backend.dto.NoteRequest;
import com.pranav.second_brain_backend.dto.NoteResponse;
import com.pranav.second_brain_backend.exception.ResourceNotFoundException;
import com.pranav.second_brain_backend.model.Note;
import com.pranav.second_brain_backend.model.Role;
import com.pranav.second_brain_backend.model.User;
import com.pranav.second_brain_backend.repository.NoteRepository;
import com.pranav.second_brain_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    private static final String EMAIL = "pranav@example.com";

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteService noteService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Pranav")
                .email(EMAIL)
                .password("encoded-password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createNoteCreatesNoteForAuthenticatedUser() {
        NoteRequest request = NoteRequest.builder()
                .title("  Architecture Notes  ")
                .content("  Keep services small  ")
                .favorite(true)
                .build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note note = invocation.getArgument(0);
            note.setId(10L);
            note.setCreatedAt(LocalDateTime.of(2026, 4, 28, 10, 30));
            return note;
        });

        NoteResponse response = noteService.createNote(request, EMAIL);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Architecture Notes");
        assertThat(response.getContent()).isEqualTo("Keep services small");
        assertThat(response.isFavorite()).isTrue();

        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(noteCaptor.capture());

        Note savedNote = noteCaptor.getValue();
        assertThat(savedNote.getUser()).isEqualTo(user);
        assertThat(savedNote.getTitle()).isEqualTo("Architecture Notes");
        assertThat(savedNote.getContent()).isEqualTo("Keep services small");
    }

    @Test
    void getAllNotesReturnsOnlyAuthenticatedUsersNotes() {
        Note first = Note.builder()
                .id(2L)
                .title("Second")
                .content("Latest note")
                .createdAt(LocalDateTime.of(2026, 4, 28, 11, 0))
                .favorite(false)
                .user(user)
                .build();

        Note second = Note.builder()
                .id(1L)
                .title("First")
                .content("Older note")
                .createdAt(LocalDateTime.of(2026, 4, 27, 11, 0))
                .favorite(true)
                .user(user)
                .build();

        when(noteRepository.findByUserEmailOrderByCreatedAtDesc(EMAIL)).thenReturn(List.of(first, second));

        List<NoteResponse> notes = noteService.getAllNotes(EMAIL);

        assertThat(notes).hasSize(2);
        assertThat(notes).extracting(NoteResponse::getTitle).containsExactly("Second", "First");
        verify(noteRepository).findByUserEmailOrderByCreatedAtDesc(EMAIL);
    }

    @Test
    void updateNoteOnlyUpdatesOwnedNote() {
        Note existingNote = Note.builder()
                .id(5L)
                .title("Old title")
                .content("Old content")
                .createdAt(LocalDateTime.of(2026, 4, 28, 9, 0))
                .favorite(false)
                .user(user)
                .build();

        NoteRequest request = NoteRequest.builder()
                .title("Updated title")
                .content("Updated content")
                .favorite(true)
                .build();

        when(noteRepository.findByIdAndUserEmail(5L, EMAIL)).thenReturn(Optional.of(existingNote));

        NoteResponse response = noteService.updateNote(5L, request, EMAIL);

        assertThat(response.getTitle()).isEqualTo("Updated title");
        assertThat(response.getContent()).isEqualTo("Updated content");
        assertThat(response.isFavorite()).isTrue();
        assertThat(existingNote.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 4, 28, 9, 0));
        verify(noteRepository).findByIdAndUserEmail(5L, EMAIL);
    }

    @Test
    void updateNoteThrowsWhenNoteDoesNotBelongToAuthenticatedUser() {
        NoteRequest request = NoteRequest.builder()
                .title("Nope")
                .content("No access")
                .build();

        when(noteRepository.findByIdAndUserEmail(99L, EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.updateNote(99L, request, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Note not found");
    }

    @Test
    void deleteNoteDeletesOnlyOwnedNote() {
        Note note = Note.builder()
                .id(7L)
                .title("Delete me")
                .content("Done")
                .createdAt(LocalDateTime.now())
                .favorite(false)
                .user(user)
                .build();

        when(noteRepository.findByIdAndUserEmail(7L, EMAIL)).thenReturn(Optional.of(note));

        noteService.deleteNote(7L, EMAIL);

        verify(noteRepository).delete(note);
    }

    @Test
    void deleteNoteDoesNotDeleteWhenNoteIsNotOwnedByAuthenticatedUser() {
        when(noteRepository.findByIdAndUserEmail(8L, EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.deleteNote(8L, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Note not found");

        verify(noteRepository, never()).delete(any(Note.class));
    }
}
