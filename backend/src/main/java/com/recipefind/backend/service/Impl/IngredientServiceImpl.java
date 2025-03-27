package com.recipefind.backend.service.Impl;

import com.recipefind.backend.dao.IngredientRepository;
import com.recipefind.backend.entity.IngredientsEntity;
import com.recipefind.backend.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {
    private final IngredientRepository ingredientsRepository;
    private final Logger logger = LoggerFactory.getLogger(IngredientServiceImpl.class);
    @Override
    public IngredientsEntity findOrSaveIngredient(String ingredientName) {
        IngredientsEntity ingredient = ingredientsRepository.findByIngredientName(ingredientName);
        if (ingredient == null) {
            ingredient = new IngredientsEntity();
            ingredient.setIngredientName(ingredientName);
            try {
                return ingredientsRepository.save(ingredient);
            } catch (Exception e)
            {
                logger.error(e.getMessage());
                return null;
            }
        } else {
            return ingredient;
        }
    }
}
