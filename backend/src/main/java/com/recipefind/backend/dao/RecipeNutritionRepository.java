package com.recipefind.backend.dao;

import com.recipefind.backend.entity.RecipeNutritionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeNutritionRepository extends JpaRepository<RecipeNutritionEntity, Integer> {

    @Query("SELECT rn.unit FROM RecipeNutritionEntity rn JOIN rn.nutritionEntity n WHERE n.nutritionName = :nutritionName")
    List<String> findNutritionUnitByNutritionName(@Param("nutritionName") String nutritionName);
}
