package com.recipefind.backend.service;

import com.recipefind.backend.dao.IngredientRepository;
import com.recipefind.backend.entity.IngredientsEntity;
import com.recipefind.backend.service.Impl.IngredientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOrSaveIngredient_IngredientExists() {
        // Given
        String ingredientName = "Tomato";
        IngredientsEntity existingIngredient = new IngredientsEntity();
        existingIngredient.setIngredientName(ingredientName);

        when(ingredientRepository.findByIngredientName(ingredientName)).thenReturn(existingIngredient);

        // When
        IngredientsEntity result = ingredientService.findOrSaveIngredient(ingredientName);

        // Then
        assertNotNull(result);
        assertEquals(ingredientName, result.getIngredientName());
        verify(ingredientRepository, times(1)).findByIngredientName(ingredientName);
        verify(ingredientRepository, never()).save(any(IngredientsEntity.class));
    }

    @Test
    void testFindOrSaveIngredient_IngredientDoesNotExist() {
        // Given
        String ingredientName = "Tomato";
        IngredientsEntity newIngredient = new IngredientsEntity();
        newIngredient.setIngredientName(ingredientName);

        when(ingredientRepository.findByIngredientName(ingredientName)).thenReturn(null);
        when(ingredientRepository.save(any(IngredientsEntity.class))).thenReturn(newIngredient);

        // When
        IngredientsEntity result = ingredientService.findOrSaveIngredient(ingredientName);

        // Then
        assertNotNull(result);
        assertEquals(ingredientName, result.getIngredientName());
        verify(ingredientRepository, times(1)).findByIngredientName(ingredientName);
        verify(ingredientRepository, times(1)).save(any(IngredientsEntity.class));
    }

    @Test
    void testFindOrSaveIngredient_SaveFails() {
        // Given
        String ingredientName = "Tomato";

        when(ingredientRepository.findByIngredientName(ingredientName)).thenReturn(null);
        when(ingredientRepository.save(any(IngredientsEntity.class))).thenThrow(new RuntimeException("Save failed"));

        // When
        IngredientsEntity result = ingredientService.findOrSaveIngredient(ingredientName);

        // Then
        assertNull(result);
        verify(ingredientRepository, times(1)).findByIngredientName(ingredientName);
        verify(ingredientRepository, times(1)).save(any(IngredientsEntity.class));
    }
}
