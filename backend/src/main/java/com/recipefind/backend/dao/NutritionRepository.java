package com.recipefind.backend.dao;

import com.recipefind.backend.entity.NutritionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionRepository extends JpaRepository<NutritionEntity, Integer> {
    NutritionEntity findByNutritionName(String nutritionName);
}
