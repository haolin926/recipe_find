package com.recipefind.backend.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.controller.RecipeController;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getRecipeByName_ShouldReturnRecipes() throws Exception {
        // Arrange
        RecipeDTO recipe = new RecipeDTO();
        recipe.setName("test");
        List<RecipeDTO> recipes = List.of(recipe);

        when(recipeService.findRecipesByName("Test")).thenReturn(recipes);

        // Act
        mockMvc.perform(get("/api/recipe/name")
                .param("queryName", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(recipes)));
    }

    @Test
    public void getRecipeByName_NotFound_ShouldReturn404() throws Exception {
        // Arrange
        List<RecipeDTO> recipes = new ArrayList<>();

        when(recipeService.findRecipesByName("Test")).thenReturn(recipes);

        // Act
        mockMvc.perform(get("/api/recipe/name")
                .param("queryName", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRecipeByName_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        when(recipeService.findRecipesByName("Test")).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(get("/api/recipe/name")
                .param("queryName", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
