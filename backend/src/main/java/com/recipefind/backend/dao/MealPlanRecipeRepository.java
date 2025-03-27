package com.recipefind.backend.dao;

import com.recipefind.backend.entity.MealPlanEntity;
import com.recipefind.backend.entity.MealPlanRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MealPlanRecipeRepository extends JpaRepository<MealPlanRecipeEntity, Integer> {

    @Query("SELECT mpr FROM MealPlanRecipeEntity mpr WHERE mpr.mealPlan.mealPlanId = :mealPlanId AND mpr.recipe.recipeId = :recipeId")
    MealPlanRecipeEntity findByMealPlan_MealPlanIdAndRecipe_RecipeId(Integer mealPlanId, Long recipeId);
}
