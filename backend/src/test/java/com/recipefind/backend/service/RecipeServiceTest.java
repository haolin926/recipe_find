package com.recipefind.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.service.Impl.RecipeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RecipeServiceTest {
    @InjectMocks
    private RecipeServiceImpl recipeService;

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeService = new RecipeServiceImpl(restTemplate, objectMapper);
    }


    @Test
    void testConstructRecipe() throws Exception {
        // Arrange
        String queryName = "chicken soup";

        // Act
        recipeService.findRecipesByName("chicken soup");
        // Assert
        // Check logs or behavior (for simplicity, ensure no exceptions were thrown)
    }

    @Test
    void testPredictImage() throws Exception {
        // Arrange
        MultipartFile image = mock(MultipartFile.class);
    }

}


