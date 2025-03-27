package com.recipefind.backend.dao;

import com.recipefind.backend.entity.RecipeIngredientsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecipeIngredientsRepository extends JpaRepository<RecipeIngredientsEntity, BigDecimal> {

    @Query("SELECT ri.ingredientUnit FROM RecipeIngredientsEntity ri JOIN ri.ingredientsEntity i WHERE i.ingredientName = :ingredientName")
    List<String> findIngredientUnitByIngredientName(@Param("ingredientName") String ingredientName);
}
