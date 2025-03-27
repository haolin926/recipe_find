package com.recipefind.backend.dao;

import com.recipefind.backend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Recipe findRecipeByRecipeApiId(Integer recipeApiId);

    Recipe findRecipeByRecipeId(Long recipeId);
}
