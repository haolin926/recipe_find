package com.recipefind.backend.Controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.controller.RecipeController;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.service.Impl.RecipeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class RecipeControllerTest {

    RecipeServiceImpl recipeService;

    @InjectMocks
    RecipeController recipeController;

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeService = new RecipeServiceImpl(restTemplate, objectMapper);
        recipeController = new RecipeController(recipeService);
    }

    @Test
    public void getRecipeByName() throws JsonProcessingException {
        // Test case for getRecipeByName
        Recipe recipe= recipeController.getRecipeByName("chicken soup");

        assertNotEquals(null, recipe);
    }

}
