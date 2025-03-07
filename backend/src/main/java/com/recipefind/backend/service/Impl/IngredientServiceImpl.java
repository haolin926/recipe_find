package com.recipefind.backend.service.Impl;

import com.recipefind.backend.dao.IngredientRepository;
import com.recipefind.backend.entity.IngredientsEntity;
import com.recipefind.backend.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {
    private final IngredientRepository ingredientsRepository;

    @Override
    public IngredientsEntity saveOrUpdateIngredient(String ingredientName) {
        IngredientsEntity ingredient = ingredientsRepository.findByIngredientName(ingredientName);
        if (ingredient == null) {
            ingredient = new IngredientsEntity();
            ingredient.setIngredientName(ingredientName);
            try {
                IngredientsEntity savedIngredient = ingredientsRepository.save(ingredient);
                return savedIngredient;
            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        } else {
            return ingredient;
        }
    }
}
