package example.service;

import example.dto.CreateNoteRequest;
import example.dto.NoteDto;
import example.entity.NoteEntity;

/**
 * Utility to perform entity <-> dto mappings for Notes.</->
 */
public class NoteMapper {

    public static NoteDto mapFromEntity(NoteEntity noteEntity){
        NoteDto noteDto = new NoteDto();
        noteDto.setId(noteEntity.getId());
        noteDto.setTitle(noteEntity.getTitle());
        noteDto.setContent(noteEntity.getContent());
        return noteDto;
    }

    public static NoteEntity mapToEntity(CreateNoteRequest createNoteRequest){
        NoteEntity entity = new NoteEntity();
        entity.setTitle(createNoteRequest.getTitle());
        entity.setContent(createNoteRequest.getContent());
        return entity;
    }
}
