package com.recipefind.backend.service.Impl;

import com.recipefind.backend.dao.SaveRecipeRepository;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.SavedRecipeEntity;
import com.recipefind.backend.entity.User;
import com.recipefind.backend.service.SaveRecipeService;
import com.recipefind.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaveRecipeServiceImpl implements SaveRecipeService {

    private final UserService userService;
    private final SaveRecipeRepository saveRecipeRepository;
    private final Logger logger = LoggerFactory.getLogger(SaveRecipeServiceImpl.class);

    @Transactional
    @Override
    public Integer saveRecipeForUser(Integer userId, Recipe recipe) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        SavedRecipeEntity savedRecipeEntity = new SavedRecipeEntity();
        savedRecipeEntity.setRecipe(recipe);
        savedRecipeEntity.setUser(user);
        try {
            saveRecipeRepository.save(savedRecipeEntity);
            return savedRecipeEntity.getSavedRecipeId();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<RecipeDTO> findByUser(Integer userId) throws Exception {
        User user = userService.getUserById(userId);

        if (user != null){
            try {
                List<SavedRecipeEntity> savedRecipes = saveRecipeRepository.findByUser(user);
                return savedRecipes.stream()
                        .map(savedRecipe -> savedRecipe.getRecipe().convertToRecipeDTO())
                        .collect(Collectors.toList());
            } catch (Exception e){
                throw new Exception(e);
            }
        } else {
            throw new Exception("User Not Found");
        }
    }

    @Transactional
    @Override
    public boolean deleteSavedRecipe(Integer userId, Integer recipeId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        try {
            int deletedRows = saveRecipeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
            return deletedRows > 0; // Returns true if delete was successful
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
