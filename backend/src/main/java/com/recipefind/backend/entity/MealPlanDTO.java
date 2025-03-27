package com.recipefind.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanDTO {
    private Integer id;
    private List<RecipeDTO> recipeDTOList;
    private List<IngredientDTO> ingredientDTOList;
    private List<NutritionDTO> nutritionDTOList;
    private Date date;
}
