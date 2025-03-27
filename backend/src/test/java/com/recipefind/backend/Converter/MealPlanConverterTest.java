package com.recipefind.backend.Converter;

import com.recipefind.backend.converter.MealPlanConverter;
import com.recipefind.backend.dao.RecipeIngredientsRepository;
import com.recipefind.backend.dao.RecipeNutritionRepository;
import com.recipefind.backend.entity.*;
import com.recipefind.backend.utils.FractionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

public class MealPlanConverterTest {
    @Mock
    private FractionConverter fractionConverter;

    @Mock
    private RecipeIngredientsRepository recipeIngredientsRepository;

    @Mock
    private RecipeNutritionRepository recipeNutritionRepository;

    @InjectMocks
    private MealPlanConverter mealPlanConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertToDTO() {
        // Given
        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(1);
        mealPlanEntity.setPlannedDate(new Date());

        Recipe recipe1 = new Recipe();
        recipe1.setRecipeId(1L);
        recipe1.setRecipeApiId(1);
        recipe1.setName("Test Recipe 1");
        recipe1.setImageUrl("test_image_url_1");

        RecipeIngredientsEntity recipeIngredientsEntity1 = new RecipeIngredientsEntity();
        IngredientsEntity ingredientsEntity1 = new IngredientsEntity();
        ingredientsEntity1.setIngredientName("Tomato");
        recipeIngredientsEntity1.setIngredientsEntity(ingredientsEntity1);
        recipeIngredientsEntity1.setIngredientAmount(new BigDecimal("2.5"));
        recipe1.setRecipeIngredientsEntities(Collections.singletonList(recipeIngredientsEntity1));

        RecipeNutritionEntity recipeNutritionEntity1 = new RecipeNutritionEntity();
        NutritionEntity nutritionEntity1 = new NutritionEntity();
        nutritionEntity1.setNutritionName("Calories");
        recipeNutritionEntity1.setNutritionEntity(nutritionEntity1);
        recipeNutritionEntity1.setAmount(new BigDecimal("200"));
        recipe1.setRecipeNutritionEntities(Collections.singletonList(recipeNutritionEntity1));

        Recipe recipe2 = new Recipe();
        recipe2.setRecipeId(2L);
        recipe2.setRecipeApiId(2);
        recipe2.setName("Test Recipe 2");
        recipe2.setImageUrl("test_image_url_2");

        RecipeIngredientsEntity recipeIngredientsEntity2 = new RecipeIngredientsEntity();
        IngredientsEntity ingredientsEntity2 = new IngredientsEntity();
        ingredientsEntity2.setIngredientName("Tomato");
        recipeIngredientsEntity2.setIngredientsEntity(ingredientsEntity2);
        recipeIngredientsEntity2.setIngredientAmount(new BigDecimal("1.5"));
        recipe2.setRecipeIngredientsEntities(Collections.singletonList(recipeIngredientsEntity2));

        RecipeNutritionEntity recipeNutritionEntity2 = new RecipeNutritionEntity();
        NutritionEntity nutritionEntity2 = new NutritionEntity();
        nutritionEntity2.setNutritionName("Calories");
        recipeNutritionEntity2.setNutritionEntity(nutritionEntity2);
        recipeNutritionEntity2.setAmount(new BigDecimal("100"));
        recipe2.setRecipeNutritionEntities(Collections.singletonList(recipeNutritionEntity2));

        MealPlanRecipeEntity mealPlanRecipeEntity1 = new MealPlanRecipeEntity();
        mealPlanRecipeEntity1.setRecipe(recipe1);
        MealPlanRecipeEntity mealPlanRecipeEntity2 = new MealPlanRecipeEntity();
        mealPlanRecipeEntity2.setRecipe(recipe2);
        mealPlanEntity.setMealPlanRecipes(Arrays.asList(mealPlanRecipeEntity1, mealPlanRecipeEntity2));


        when(fractionConverter.decimalToFraction(anyDouble())).thenReturn("4");
        when(recipeIngredientsRepository.findIngredientUnitByIngredientName("Tomato")).thenReturn(Collections.singletonList("kg"));
        when(recipeNutritionRepository.findNutritionUnitByNutritionName("Calories")).thenReturn(Collections.singletonList("kcal"));

        // When
        MealPlanDTO mealPlanDTO = MealPlanConverter.convertToDTO(mealPlanEntity);

        // Then
        assertNotNull(mealPlanDTO);
        assertEquals(2, mealPlanDTO.getRecipeDTOList().size());
        assertEquals("Test Recipe 1", mealPlanDTO.getRecipeDTOList().get(0).getName());
        assertEquals("Test Recipe 2", mealPlanDTO.getRecipeDTOList().get(1).getName());
        assertEquals("4", mealPlanDTO.getIngredientDTOList().get(0).getAmount());
        assertEquals("kg", mealPlanDTO.getIngredientDTOList().get(0).getUnit());
        assertEquals("300", mealPlanDTO.getNutritionDTOList().get(0).getAmount());
        assertEquals("kcal", mealPlanDTO.getNutritionDTOList().get(0).getUnit());
    }

    @Test
    void testConvertDTOListToWeeklySummary() {
        // Given
        IngredientDTO ingredientDTO1 = new IngredientDTO();
        ingredientDTO1.setName("Tomato");
        ingredientDTO1.setAmount("2 1/2");
        ingredientDTO1.setUnit("kg");

        NutritionDTO nutritionDTO1 = new NutritionDTO();
        nutritionDTO1.setName("Calories");
        nutritionDTO1.setAmount("200");
        nutritionDTO1.setUnit("kcal");

        MealPlanDTO mealPlanDTO1 = new MealPlanDTO();
        mealPlanDTO1.setIngredientDTOList(Collections.singletonList(ingredientDTO1));
        mealPlanDTO1.setNutritionDTOList(Collections.singletonList(nutritionDTO1));

        IngredientDTO ingredientDTO2 = new IngredientDTO();
        ingredientDTO2.setName("Tomato");
        ingredientDTO2.setAmount("1 1/2");
        ingredientDTO2.setUnit("kg");

        NutritionDTO nutritionDTO2 = new NutritionDTO();
        nutritionDTO2.setName("Calories");
        nutritionDTO2.setAmount("100");
        nutritionDTO2.setUnit("kcal");

        MealPlanDTO mealPlanDTO2 = new MealPlanDTO();
        mealPlanDTO2.setIngredientDTOList(Collections.singletonList(ingredientDTO2));
        mealPlanDTO2.setNutritionDTOList(Collections.singletonList(nutritionDTO2));

        List<MealPlanDTO> mealPlanDTOList = Arrays.asList(mealPlanDTO1, mealPlanDTO2);

        when(fractionConverter.fractionToDecimal("2 1/2")).thenReturn(new BigDecimal("2.5"));
        when(fractionConverter.fractionToDecimal("1 1/2")).thenReturn(new BigDecimal("1.5"));
        when(fractionConverter.decimalToFraction(4.0)).thenReturn("4");

        // When
        MealPlanWeeklySummaryDTO weeklySummaryDTO = MealPlanConverter.convertDTOListToWeeklySummary(mealPlanDTOList);

        // Then
        assertNotNull(weeklySummaryDTO);
        assertEquals(1, weeklySummaryDTO.getIngredientDTOList().size());
        assertEquals("Tomato", weeklySummaryDTO.getIngredientDTOList().get(0).getName());
        assertEquals("4", weeklySummaryDTO.getIngredientDTOList().get(0).getAmount());
        assertEquals("kg", weeklySummaryDTO.getIngredientDTOList().get(0).getUnit());
        assertEquals(1, weeklySummaryDTO.getNutritionDTOList().size());
        assertEquals("Calories", weeklySummaryDTO.getNutritionDTOList().get(0).getName());
        assertEquals("300", weeklySummaryDTO.getNutritionDTOList().get(0).getAmount());
        assertEquals("kcal", weeklySummaryDTO.getNutritionDTOList().get(0).getUnit());
    }
}
