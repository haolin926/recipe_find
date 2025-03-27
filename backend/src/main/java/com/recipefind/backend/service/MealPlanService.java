package com.recipefind.backend.service;

import com.recipefind.backend.entity.MealPlanDTO;
import com.recipefind.backend.entity.MealPlanEntity;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.MealPlanWeeklySummaryDTO;
import java.util.Date;

public interface MealPlanService {
    MealPlanDTO getMealPlanForUserOnDate (Integer userId, Date date) throws Exception;

    MealPlanEntity AddRecipeIntoMealPlan(Integer userId, Date date, RecipeDTO recipeDTO);

    MealPlanDTO DeleteRecipeForMealPlan(Integer mealPlanId, Integer recipeId);

    MealPlanWeeklySummaryDTO getMealPlanForUserOnCurrentWeek(Integer userId, Date date) throws Exception;
}
