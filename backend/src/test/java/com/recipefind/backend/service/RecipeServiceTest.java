package com.recipefind.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.dao.RecipeRepository;
import com.recipefind.backend.entity.*;
import com.recipefind.backend.service.Impl.RecipeServiceImpl;
import com.recipefind.backend.utils.FractionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class RecipeServiceTest {
    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private SaveRecipeService saveRecipeService;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private NutritionService nutritionService;

    @Mock
    private UserService userService;

    @Mock
    private FractionConverter fractionConverter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveFavouriteRecipe() {
        // Arrange
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);
        recipeDTO.setName("Test Recipe");
        recipeDTO.setImage("test_image.jpg");

        IngredientDTO ingredientDTO = new IngredientDTO(1, "Test Ingredient", "1", "cup");
        List<IngredientDTO> ingredientDTOS = new ArrayList<>();
        ingredientDTOS.add(ingredientDTO);
        recipeDTO.setIngredientDTOS(ingredientDTOS);

        NutritionDTO nutritionDTO = new NutritionDTO("Calories", "100", "kcal");
        List<NutritionDTO> nutritionDTOS = new ArrayList<>();
        nutritionDTOS.add(nutritionDTO);
        recipeDTO.setNutritionDTOS(nutritionDTOS);

        List<String> instructions = new ArrayList<>();
        instructions.add("step1");
        instructions.add("step2");
        recipeDTO.setInstructions(instructions);

        User user = new User();
        user.setId(1L);

        when(recipeRepository.findRecipeByRecipeApiId(anyInt())).thenReturn(null);
        when(ingredientService.saveOrUpdateIngredient(anyString())).thenReturn(new IngredientsEntity());
        when(nutritionService.saveOrUpdateNutrition(anyString())).thenReturn(new NutritionEntity());
        when(saveRecipeService.saveRecipeForUser(anyInt(), any(Recipe.class))).thenReturn(1);

        // Act
        Integer result = recipeService.saveFavouriteRecipe(recipeDTO, 1);

        // Assert
        assertNotNull(result);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
        verify(saveRecipeService, times(1)).saveRecipeForUser(anyInt(), any(Recipe.class));
    }
}


