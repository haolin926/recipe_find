package com.recipefind.backend.service.Impl;

import com.recipefind.backend.dao.NutritionRepository;
import com.recipefind.backend.entity.NutritionEntity;
import com.recipefind.backend.service.NutritionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {
    private final NutritionRepository nutritionRepository;

    @Override
    public NutritionEntity findOrSaveNutrition(String nutritionName) {
        NutritionEntity nutrition = nutritionRepository.findByNutritionName(nutritionName);
        if (nutrition == null) {
            nutrition = new NutritionEntity();
            nutrition.setNutritionName(nutritionName);
            nutrition = nutritionRepository.save(nutrition);
        }
        return nutrition;
    }

}
