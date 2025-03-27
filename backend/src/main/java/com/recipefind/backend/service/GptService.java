package com.recipefind.backend.service;

import com.recipefind.backend.entity.RecipeDTO;

import java.util.List;

public interface GptService {
    List<RecipeDTO> constructRecipeDescription(List<RecipeDTO> recipes);

    List<String> validateFoodItems (List<String> word);
}
