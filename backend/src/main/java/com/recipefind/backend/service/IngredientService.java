package com.recipefind.backend.service;

import com.recipefind.backend.entity.IngredientsEntity;

public interface IngredientService {
    IngredientsEntity findOrSaveIngredient(String ingredientName);
}
