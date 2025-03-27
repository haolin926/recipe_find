package com.recipefind.backend.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.controller.RecipeController;
import com.recipefind.backend.entity.PredictResult;
import com.recipefind.backend.entity.Prediction;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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


    @Test
    public void getRecipeByImage_ShouldReturnPrediction() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        PredictResult predictResult = new PredictResult();
        List<Prediction> predictions = new ArrayList<>();
        List<String> detectedIngredients = new ArrayList<>();
        predictResult.setPredictName(predictions);
        predictResult.setDetectedIngredients(detectedIngredients);

        when(recipeService.imagePrediction(image)).thenReturn(predictResult);

        // Act
        mockMvc.perform(multipart("/api/recipe/image")
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(predictResult)));
    }

    @Test
    public void getRecipeByImage_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(recipeService.imagePrediction(image)).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(multipart("/api/recipe/image")
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }


    @Test
    public void getRecipeById_ShouldReturnRecipe() throws Exception {
        // Arrange
        RecipeDTO recipe = new RecipeDTO();
        recipe.setId(1);
        recipe.setName("test recipe");

        when(recipeService.findRecipeInSpoonacularByApiId(1)).thenReturn(recipe);

        // Act
        mockMvc.perform(get("/api/recipe/id")
                        .param("queryId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(recipe)));
    }

    @Test
    public void getRecipeById_NotFound_ShouldReturn404() throws Exception {
        // Arrange
        when(recipeService.findRecipeInSpoonacularByApiId(1)).thenReturn(null);

        // Act
        mockMvc.perform(get("/api/recipe/id")
                        .param("queryId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveRecipe_ShouldReturnOk() throws Exception {
        // Arrange
        RecipeDTO recipe = new RecipeDTO();
        recipe.setId(1);
        recipe.setName("test recipe");

        when(recipeService.saveFavouriteRecipe(recipe, 1)).thenReturn(1);

        // Act
        mockMvc.perform(post("/api/recipe/save")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void saveRecipe_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        RecipeDTO recipe = new RecipeDTO();
        recipe.setId(1);
        recipe.setName("test recipe");

        when(recipeService.saveFavouriteRecipe(recipe, 1)).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(post("/api/recipe/save")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isInternalServerError());
    }
    @Test
    public void searchRecipeByIngredients_ShouldReturnRecipes() throws Exception {
        // Arrange
        List<String> ingredients = List.of("tomato", "cheese");
        RecipeDTO recipe = new RecipeDTO();
        recipe.setName("test recipe");
        List<RecipeDTO> recipes = List.of(recipe);

        when(recipeService.findRecipesByIngredients(ingredients)).thenReturn(recipes);

        // Act
        mockMvc.perform(get("/api/recipe/searchByIngredients")
                        .param("ingredients", "tomato,cheese")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(recipes)));
    }

    @Test
    public void searchRecipeByIngredients_NotFound_ShouldReturn404() throws Exception {
        // Arrange
        List<String> ingredients = List.of("tomato", "cheese");

        when(recipeService.findRecipesByIngredients(ingredients)).thenReturn(null);

        // Act
        mockMvc.perform(get("/api/recipe/searchByIngredients")
                        .param("ingredients", "tomato,cheese")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchRecipeByIngredients_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        List<String> ingredients = List.of("tomato", "cheese");

        Mockito.when(recipeService.findRecipesByIngredients(ingredients)).thenThrow(new JsonProcessingException("Simulated server error") {});

        // Act
        mockMvc.perform(get("/api/recipe/searchByIngredients")
                        .param("ingredients", "tomato,cheese")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}
