package example.repository;

import example.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotesRepository extends JpaRepository<NoteEntity, Long> {
}
