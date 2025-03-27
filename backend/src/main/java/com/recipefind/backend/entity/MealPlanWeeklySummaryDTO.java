package com.recipefind.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanWeeklySummaryDTO {
    private List<IngredientDTO> ingredientDTOList;
    private List<NutritionDTO> nutritionDTOList;
}
