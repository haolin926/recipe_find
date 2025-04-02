package com.recipefind.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefind.backend.dao.CommentRepository;
import com.recipefind.backend.entity.Comment;
import com.recipefind.backend.entity.CommentDTO;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.entity.User;
import com.recipefind.backend.service.Impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class CommentServiceTest {


    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RecipeService recipeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveComment_Success() throws JsonProcessingException {
        // Given
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setRecipeApiId(1);
        commentDTO.setUserId(1);
        commentDTO.setComment("Great recipe!");
        commentDTO.setImages(Collections.emptyList());
        commentDTO.setRate(5.0f);

        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Comment comment = new Comment();
        comment.setCommentId(1);
        comment.setUserId(1);
        comment.setCommentContent("Great recipe!");
        comment.setImages(Collections.emptyList());
        comment.setRecipeId(1);
        comment.setRate(5.0f);

        when(userService.getUserById(1)).thenReturn(user);
        when(recipeService.findRecipeByApiId(1)).thenReturn(recipe);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(recipeService.updateRecipeRate(1L, 5.0f)).thenReturn(true);
        // When
        Integer result = commentService.saveComment(commentDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result);
        verify(recipeService, times(1)).updateRecipeRate(1L, 5.0f);
    }

    @Test
    void testSaveComment_UserNotFound() {
        // Given
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setRecipeApiId(1);
        commentDTO.setUserId(1);

        when(userService.getUserById(1)).thenReturn(null);

        // When / Then
        assertThrows(RuntimeException.class, () -> commentService.saveComment(commentDTO));
    }

    @Test
    void testSaveComment_RepositorySaveFail() throws JsonProcessingException {
        // Given
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setRecipeApiId(1);
        commentDTO.setUserId(1);
        commentDTO.setComment("Great recipe!");
        commentDTO.setImages(Collections.emptyList());
        commentDTO.setRate(5.0f);

        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.getUserById(1)).thenReturn(user);
        when(recipeService.findRecipeByApiId(1)).thenReturn(recipe);
        when(commentRepository.save(any(Comment.class))).thenThrow(new RuntimeException("Failed to save comment!"));

        // When / Then
        Integer result = commentService.saveComment(commentDTO);
        assertNull(result);
    }

    @Test
    void testSaveComment_UpdateRecipeRateFail() throws JsonProcessingException {
        // Given
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setRecipeApiId(1);
        commentDTO.setUserId(1);
        commentDTO.setComment("Great recipe!");
        commentDTO.setImages(Collections.emptyList());
        commentDTO.setRate(5.0f);

        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Comment comment = new Comment();
        comment.setCommentId(1);
        comment.setUserId(1);
        comment.setCommentContent("Great recipe!");
        comment.setImages(Collections.emptyList());
        comment.setRecipeId(1);
        comment.setRate(5.0f);

        when(userService.getUserById(1)).thenReturn(user);
        when(recipeService.findRecipeByApiId(1)).thenReturn(recipe);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(recipeService.updateRecipeRate(1L, 5.0f)).thenReturn(false);

        // When / Then
        Integer result = commentService.saveComment(commentDTO);
        assertNull(result);
    }

    @Test
    void testGetCommentsForRecipe_Success() {
        // Given
        Integer recipeApiId = 1;
        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        Comment comment = new Comment();
        comment.setUserId(1);
        comment.setCommentContent("Great recipe!");
        comment.setImages(Collections.emptyList());
        comment.setRate(5.0f);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setUserPhoto("photo_url");

        when(recipeService.findRecipeByApiId(recipeApiId)).thenReturn(recipe);
        when(commentRepository.getCommentsByRecipeId(1)).thenReturn(Collections.singletonList(comment));
        when(userService.getUserById(1)).thenReturn(user);

        // When
        List<CommentDTO> result = commentService.getCommentsForRecipe(recipeApiId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Great recipe!", result.get(0).getComment());
        assertEquals("testuser", result.get(0).getUserName());
        assertEquals("photo_url", result.get(0).getUserPhoto());
    }

    @Test
    void testGetCommentsForRecipe_RecipeNotFound() {
        // Given
        Integer recipeApiId = 1;

        when(recipeService.findRecipeByApiId(recipeApiId)).thenReturn(null);

        // When
        List<CommentDTO> result = commentService.getCommentsForRecipe(recipeApiId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
