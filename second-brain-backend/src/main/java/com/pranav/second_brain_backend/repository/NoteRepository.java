package com.pranav.second_brain_backend.repository;

import com.pranav.second_brain_backend.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserEmailOrderByCreatedAtDesc(String email);

    Optional<Note> findByIdAndUserEmail(Long id, String email);
}
