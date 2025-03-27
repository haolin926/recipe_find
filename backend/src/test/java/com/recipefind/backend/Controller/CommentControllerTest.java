package com.recipefind.backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.controller.CommentController;
import com.recipefind.backend.entity.CommentDTO;
import com.recipefind.backend.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void postComment_ShouldReturnSavedCommentId() throws Exception {
        // Arrange
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setRecipeApiId(1);
        commentDTO.setComment("Great recipe!");

        when(commentService.saveComment(commentDTO)).thenReturn(1);

        // Act
        mockMvc.perform(post("/api/comment/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void postComment_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setRecipeApiId(1);
        commentDTO.setComment("Great recipe!");

        when(commentService.saveComment(commentDTO)).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(post("/api/comment/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to save comment"));
    }

    @Test
    public void getCommentForRecipe_ShouldReturnComments() throws Exception {
        // Arrange
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setRecipeApiId(1);
        commentDTO.setComment("Great recipe!");
        List<CommentDTO> comments = List.of(commentDTO);

        when(commentService.getCommentsForRecipe(1)).thenReturn(comments);

        // Act
        mockMvc.perform(get("/api/comment")
                        .param("recipeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comments)));
    }

    @Test
    public void getCommentForRecipe_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        when(commentService.getCommentsForRecipe(1)).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(get("/api/comment")
                        .param("recipeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to get comments for current recipe"));
    }
}
