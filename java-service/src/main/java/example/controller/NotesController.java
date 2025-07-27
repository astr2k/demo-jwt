package example.controller;

import example.dto.CreateNoteRequest;
import example.dto.NoteDto;
import example.service.NotesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for the Notes resource.
 */
@RestController
@SecurityRequirement(name = "Notes Authorization")
@Validated
@Tag(name = "Notes")
public class NotesController {

    private final NotesService notesService;

    public NotesController(@Autowired NotesService notesService) {
        this.notesService = notesService;
    }

    @GetMapping("/notes")
    @Operation(description = "Retrieves all notes.")
    public ResponseEntity<List<NoteDto>> retrieveNotes() {
        return new ResponseEntity<>(notesService.retrieveAll(), HttpStatus.OK);
    }

    @PostMapping("/notes")
    @Operation(description = "Creates new note.")
    public ResponseEntity<Long> createNote(@RequestBody @Valid CreateNoteRequest request) {
        return new ResponseEntity<>(notesService.createNote(request), HttpStatus.CREATED);
    }

}
