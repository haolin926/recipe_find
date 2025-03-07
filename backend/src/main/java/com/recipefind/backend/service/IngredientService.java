package com.recipefind.backend.service;

import com.recipefind.backend.entity.IngredientsEntity;

public interface IngredientService {
    IngredientsEntity saveOrUpdateIngredient(String ingredientName);
}
