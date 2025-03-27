package com.recipefind.backend.service;

import com.recipefind.backend.entity.NutritionEntity;

public interface NutritionService {
    NutritionEntity findOrSaveNutrition(String nutritionName);
}
