package example.service;

import example.dto.CreateNoteRequest;
import example.dto.NoteDto;
import example.entity.NoteEntity;
import example.repository.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to perform search, create operations for Notes.
 */
@Component
public class NotesService {
    private final NotesRepository notesRepository;

    public NotesService(@Autowired NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public NoteEntity createNote(NoteEntity noteEntity){
        return notesRepository.save(noteEntity);
    }

    public Long createNote(CreateNoteRequest createNoteRequest){
        NoteEntity created = createNote(NoteMapper.mapToEntity(createNoteRequest));
        return created.getId();
    }

    public List<NoteEntity> findAll(){
        return notesRepository.findAll();
    }

    public List<NoteDto> retrieveAll(){
        return notesRepository.findAll().stream()
                .map(NoteMapper::mapFromEntity)
                .collect(Collectors.toList());
    }
}
