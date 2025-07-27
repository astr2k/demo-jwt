package example.controller;

import example.entity.NoteEntity;
import example.repository.NotesRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link NotesController}
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotesControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    NotesRepository notesRepository;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void when_Authenticated_then_SuccessfullyRetirevesNotes() throws Exception {
        MvcResult result = mvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                	{
                                		"login": "user2",
                                		"password": "pwd2"
                                	}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        // Database will be prepopulated (liquibase) with some notes, so we can expect these ids
        Integer[] expectedIds = new Integer[]{1, 2, 3, 4, 5};

        String token = result.getResponse().getContentAsString();
        mvc.perform(get("/notes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.not(Matchers.empty())))
                .andExpect(jsonPath("$..id", Matchers.hasItems(expectedIds)))
                .andReturn();
    }

    @Test
    void when_AuthenticatedAndHasWriteAuthority_then_SuccessfullyCreatesNote() throws Exception {
        MvcResult result = mvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                	{
                                		"login": "user2",
                                		"password": "pwd2"
                                	}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();
        result = mvc.perform(post("/notes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                	{
                                		"title": "Test title",
                                		"content": "Test content"
                                	}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        Long newId = Long.valueOf(result.getResponse().getContentAsString());
        Assertions.assertTrue(notesRepository.findById(newId).isPresent());

        NoteEntity newNote = notesRepository.findById(newId).get();
        Assertions.assertEquals("Test title", newNote.getTitle());
        Assertions.assertEquals("Test content", newNote.getContent());

    }

}