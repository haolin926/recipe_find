package com.recipefind.backend.service;

import com.recipefind.backend.dao.NutritionRepository;
import com.recipefind.backend.entity.NutritionEntity;
import com.recipefind.backend.service.Impl.NutritionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class NutritionServiceTest {


    @InjectMocks
    private NutritionServiceImpl nutritionService;

    @Mock
    private NutritionRepository nutritionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOrSaveNutrition_SaveNewNutrition() {
        // Arrange
        String nutritionName = "Protein";
        when(nutritionRepository.findByNutritionName(nutritionName)).thenReturn(null);
        NutritionEntity newNutrition = new NutritionEntity();
        newNutrition.setNutritionName(nutritionName);
        when(nutritionRepository.save(any(NutritionEntity.class))).thenReturn(newNutrition);

        // Act
        NutritionEntity result = nutritionService.findOrSaveNutrition(nutritionName);

        // Assert
        assertNotNull(result);
        assertEquals(nutritionName, result.getNutritionName());
        verify(nutritionRepository, times(1)).findByNutritionName(nutritionName);
        verify(nutritionRepository, times(1)).save(any(NutritionEntity.class));
    }

    @Test
    void testFindOrSaveutrition_UpdateExistingNutrition() {
        // Arrange
        String nutritionName = "Protein";
        NutritionEntity existingNutrition = new NutritionEntity();
        existingNutrition.setNutritionName(nutritionName);
        when(nutritionRepository.findByNutritionName(nutritionName)).thenReturn(existingNutrition);

        // Act
        NutritionEntity result = nutritionService.findOrSaveNutrition(nutritionName);

        // Assert
        assertNotNull(result);
        assertEquals(nutritionName, result.getNutritionName());
        verify(nutritionRepository, times(1)).findByNutritionName(nutritionName);
        verify(nutritionRepository, never()).save(any(NutritionEntity.class));
    }
}
