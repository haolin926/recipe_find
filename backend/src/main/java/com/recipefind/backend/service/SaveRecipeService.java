package com.recipefind.backend.service;

import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.User;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SaveRecipeService {
    @Transactional
    Integer saveRecipeForUser(Integer userId, Recipe recipe);

    @Transactional
    List<RecipeDTO> findByUser(Integer userId) throws Exception;

    @Transactional
    boolean deleteSavedRecipe(Integer userId, Integer recipeId);

    boolean isRecipeSavedByUser(User user, Recipe recipe);
}
