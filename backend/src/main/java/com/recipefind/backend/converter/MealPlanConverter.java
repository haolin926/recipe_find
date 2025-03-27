package com.recipefind.backend.converter;


import com.recipefind.backend.dao.RecipeIngredientsRepository;
import com.recipefind.backend.dao.RecipeNutritionRepository;
import com.recipefind.backend.entity.*;
import com.recipefind.backend.utils.FractionConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class MealPlanConverter {
    private static FractionConverter fractionConverter;

    private static RecipeIngredientsRepository recipeIngredientsRepository;

    private static RecipeNutritionRepository recipeNutritionRepository;

    @Autowired
    public MealPlanConverter(FractionConverter fractionConverter,
                             RecipeIngredientsRepository recipeIngredientsRepository,
                             RecipeNutritionRepository recipeNutritionRepository) {
        MealPlanConverter.fractionConverter = fractionConverter;
        MealPlanConverter.recipeIngredientsRepository = recipeIngredientsRepository;
        MealPlanConverter.recipeNutritionRepository = recipeNutritionRepository;
    }

    public static MealPlanDTO convertToDTO (MealPlanEntity mealPlanEntity) {
        List<Recipe> recipes = mealPlanEntity
                .getMealPlanRecipes().stream()
                .map(MealPlanRecipeEntity::getRecipe)
                .toList();


        List<RecipeDTO> recipeDTOS = new ArrayList<>();
        Map<String, BigDecimal> ingredientSumList = new HashMap<>();
        Map<String, BigDecimal> nutritionSumList = new HashMap<>();

        for (Recipe recipe : recipes) {
            RecipeDTO recipeDTO = new RecipeDTO();
            recipeDTO.setId(recipe.getRecipeId().intValue());
            recipeDTO.setRecipeApiId(recipe.getRecipeApiId());
            recipeDTO.setName(recipe.getName());
            recipeDTO.setImage(recipe.getImageUrl());

            recipeDTOS.add(recipeDTO);

            for (RecipeIngredientsEntity recipeIngredientsEntity : recipe.getRecipeIngredientsEntities()) {

                String ingredientName = recipeIngredientsEntity.getIngredientsEntity().getIngredientName();
                BigDecimal ingredientAmount = recipeIngredientsEntity.getIngredientAmount();

                if (ingredientSumList.containsKey(ingredientName)) {
                    BigDecimal newAmount = ingredientSumList.get(ingredientName).add(ingredientAmount);
                    ingredientSumList.put(ingredientName, newAmount);

                } else {
                    ingredientSumList.put(ingredientName, ingredientAmount);
                }
            }

            for (RecipeNutritionEntity recipeNutritionEntity : recipe.getRecipeNutritionEntities()) {
                String nutritionName = recipeNutritionEntity.getNutritionEntity().getNutritionName();
                BigDecimal nutritionAmount = recipeNutritionEntity.getAmount();

                if (nutritionSumList.containsKey(nutritionName)) {
                    BigDecimal newAmount = nutritionSumList.get(nutritionName).add(nutritionAmount);
                    nutritionSumList.put(nutritionName, newAmount);

                } else {
                    nutritionSumList.put(nutritionName, nutritionAmount);
                }
            }

        }

        List<IngredientDTO> ingredientDTOList = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : ingredientSumList.entrySet()) {
            IngredientDTO ingredientDTO = new IngredientDTO();
            ingredientDTO.setName(entry.getKey());
            List<String> ingredientUnits = recipeIngredientsRepository.findIngredientUnitByIngredientName(entry.getKey());
            ingredientDTO.setAmount(fractionConverter.decimalToFraction(entry.getValue().doubleValue()));
            ingredientDTO.setUnit(ingredientUnits.isEmpty() ? null : ingredientUnits.get(0));

            ingredientDTOList.add(ingredientDTO);
        }

        List<NutritionDTO> nutritionDTOList = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : nutritionSumList.entrySet()) {
            NutritionDTO nutritionDTO = new NutritionDTO();
            nutritionDTO.setName(entry.getKey());
            nutritionDTO.setAmount(entry.getValue().toString());
            List<String> nutritionUnits = recipeNutritionRepository.findNutritionUnitByNutritionName(entry.getKey());
            nutritionDTO.setUnit(nutritionUnits.isEmpty() ? null : nutritionUnits.get(0));

            nutritionDTOList.add(nutritionDTO);
        }

        MealPlanDTO mealPlanDTO = new MealPlanDTO();
        mealPlanDTO.setRecipeDTOList(recipeDTOS);
        mealPlanDTO.setId(mealPlanEntity.getMealPlanId());
        mealPlanDTO.setDate(mealPlanEntity.getPlannedDate());
        mealPlanDTO.setIngredientDTOList(ingredientDTOList);
        mealPlanDTO.setNutritionDTOList(nutritionDTOList);

        return mealPlanDTO;
    }

    public static MealPlanWeeklySummaryDTO convertDTOListToWeeklySummary(List<MealPlanDTO> mealPlanDTOList) {
        Map<String, IngredientDTO> ingredientDTOMap = new HashMap<>();
        Map<String, NutritionDTO> nutritionDTOMap = new HashMap<>();

        for (MealPlanDTO mealPlanDTO : mealPlanDTOList) {
            for (IngredientDTO ingredientDTO : mealPlanDTO.getIngredientDTOList()) {
                String ingredientName = ingredientDTO.getName();
                BigDecimal amount =  fractionConverter.fractionToDecimal(ingredientDTO.getAmount());
                String unit = ingredientDTO.getUnit();

                if (ingredientDTOMap.containsKey(ingredientName)) {
                    IngredientDTO existingIngredientDTO = ingredientDTOMap.get(ingredientName);
                    BigDecimal existingAmount =  fractionConverter.fractionToDecimal(existingIngredientDTO.getAmount());
                    BigDecimal newAmountInDecimal = existingAmount.add(amount);
                    String newAmountInFraction = fractionConverter.decimalToFraction(newAmountInDecimal.doubleValue());

                    IngredientDTO newIngredientDTO = new IngredientDTO(ingredientName, newAmountInFraction, unit);
                    ingredientDTOMap.put(ingredientName, newIngredientDTO);
                } else {
                    ingredientDTOMap.put(ingredientName, ingredientDTO);
                }
            }

            for (NutritionDTO nutritionDTO : mealPlanDTO.getNutritionDTOList()) {
                String nutritionName  = nutritionDTO.getName();
                BigDecimal nutritionAmount = new BigDecimal(nutritionDTO.getAmount());
                String unit = nutritionDTO.getUnit();

                if (nutritionDTOMap.containsKey(nutritionName)) {
                    NutritionDTO exisitingNutritionDTO = nutritionDTOMap.get(nutritionName);
                    BigDecimal existingAmount = new BigDecimal(exisitingNutritionDTO.getAmount());
                    BigDecimal newAmount = existingAmount.add(nutritionAmount);
                    String newAmountInString = newAmount.toString();

                    NutritionDTO newNutritionDTO = new NutritionDTO(nutritionName, newAmountInString, unit);
                    nutritionDTOMap.put(nutritionName, newNutritionDTO);
                } else {
                    nutritionDTOMap.put(nutritionName, nutritionDTO);
                }
            }
        }

        List<IngredientDTO> ingredientDTOList = ingredientDTOMap.values().stream().toList();
        List<NutritionDTO> nutritionDTOList = nutritionDTOMap.values().stream().toList();

        return new MealPlanWeeklySummaryDTO(ingredientDTOList, nutritionDTOList);

    }
}
