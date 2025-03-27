package com.recipefind.backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.controller.SavedRecipeController;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.service.SaveRecipeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SavedRecipeController.class)
public class SavedRecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaveRecipeService saveRecipeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getSavedRecipes_ShouldReturnRecipes() throws Exception {
        // Arrange
        RecipeDTO recipe = new RecipeDTO();
        recipe.setId(1);
        recipe.setName("test recipe");
        List<RecipeDTO> recipes = List.of(recipe);

        when(saveRecipeService.findByUser(1)).thenReturn(recipes);

        // Act
        mockMvc.perform(get("/api/savedRecipe")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(recipes)));
    }

    @Test
    public void getSavedRecipes_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        when(saveRecipeService.findByUser(1)).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(get("/api/savedRecipe")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteSavedRecipe_ShouldReturnOk() throws Exception {
        // Arrange
        when(saveRecipeService.deleteSavedRecipe(1, 1)).thenReturn(true);

        // Act
        mockMvc.perform(delete("/api/savedRecipe/delete")
                        .param("userId", "1")
                        .param("recipeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteSavedRecipe_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        when(saveRecipeService.deleteSavedRecipe(1, 1)).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(delete("/api/savedRecipe/delete")
                        .param("userId", "1")
                        .param("recipeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
