package com.recipefind.backend.dao;

import com.recipefind.backend.entity.IngredientsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientsEntity, Integer> {
    IngredientsEntity findByIngredientName(String ingredientName);
}
