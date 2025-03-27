package com.recipefind.backend.service;

import com.recipefind.backend.dao.SaveRecipeRepository;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.SavedRecipeEntity;
import com.recipefind.backend.entity.User;
import com.recipefind.backend.service.Impl.SaveRecipeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class SaveRecipeServiceTest {

    @InjectMocks
    private SaveRecipeServiceImpl saveRecipeService;

    @Mock
    private UserService userService;

    @Mock
    private SaveRecipeRepository saveRecipeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveRecipeForUser_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Recipe recipe = new Recipe();
        SavedRecipeEntity savedRecipeEntity = new SavedRecipeEntity();
        savedRecipeEntity.setRecipe(recipe);
        savedRecipeEntity.setUser(user);

        when(userService.getUserById(1)).thenReturn(user);
        when(saveRecipeRepository.save(any(SavedRecipeEntity.class))).thenReturn(savedRecipeEntity);

        // Act
        Integer result = saveRecipeService.saveRecipeForUser(1, recipe);

        // Assert
        assertNotNull(result);
        assertEquals(0, result);
        verify(userService, times(1)).getUserById(1);
        verify(saveRecipeRepository, times(1)).save(any(SavedRecipeEntity.class));
    }

    @Test
    void testSaveRecipeForUser_UserNotFound() {
        // Arrange
        when(userService.getUserById(1)).thenReturn(null);
        Recipe recipe = new Recipe();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            saveRecipeService.saveRecipeForUser(1, recipe);
        });
        verify(userService, times(1)).getUserById(1);
        verify(saveRecipeRepository, never()).save(any(SavedRecipeEntity.class));
    }

    @Test
    void testFindByUser_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);

        Recipe recipe = mock(Recipe.class);
        RecipeDTO recipeDTO = new RecipeDTO();
        SavedRecipeEntity savedRecipeEntity = new SavedRecipeEntity();
        savedRecipeEntity.setRecipe(recipe);
        savedRecipeEntity.setUser(user);

        List<SavedRecipeEntity> savedRecipes = new ArrayList<>();
        savedRecipes.add(savedRecipeEntity);

        when(userService.getUserById(1)).thenReturn(user);
        when(saveRecipeRepository.findByUser(user)).thenReturn(savedRecipes);
        when(recipe.convertToRecipeDTO()).thenReturn(recipeDTO);

        // Act
        List<RecipeDTO> result = saveRecipeService.findByUser(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recipeDTO, result.get(0));
        verify(userService, times(1)).getUserById(1);
        verify(saveRecipeRepository, times(1)).findByUser(user);
    }

    @Test
    void testFindByUser_UserNotFound() {
        // Arrange
        when(userService.getUserById(1)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            saveRecipeService.findByUser(1);
        });
        assertEquals("User Not Found", exception.getMessage());
        verify(userService, times(1)).getUserById(1);
        verify(saveRecipeRepository, never()).findByUser(any(User.class));
    }

    @Test
    void testDeleteSavedRecipe_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);

        when(userService.getUserById(1)).thenReturn(user);
        when(saveRecipeRepository.deleteByUserIdAndRecipeId(1, 1)).thenReturn(1);

        // Act
        boolean result = saveRecipeService.deleteSavedRecipe(1, 1);

        // Assert
        assertTrue(result);
        verify(userService, times(1)).getUserById(1);
        verify(saveRecipeRepository, times(1)).deleteByUserIdAndRecipeId(1, 1);
    }

    @Test
    void testDeleteSavedRecipe_UserNotFound() {
        // Arrange
        when(userService.getUserById(1)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            saveRecipeService.deleteSavedRecipe(1, 1);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).getUserById(1);
        verify(saveRecipeRepository, never()).deleteByUserIdAndRecipeId(anyInt(), anyInt());
    }

    @Test
    void testDeleteSavedRecipe_DeleteFailed() {
        // Arrange
        User user = new User();
        user.setId(1L);

        when(userService.getUserById(1)).thenReturn(user);
        when(saveRecipeRepository.deleteByUserIdAndRecipeId(1, 1)).thenReturn(0);

        // Act
        boolean result = saveRecipeService.deleteSavedRecipe(1, 1);

        // Assert
        assertFalse(result);
        verify(userService, times(1)).getUserById(1);
        verify(saveRecipeRepository, times(1)).deleteByUserIdAndRecipeId(1, 1);
    }
}
