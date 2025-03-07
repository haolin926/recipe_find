package com.recipefind.backend.service.Impl;

import com.recipefind.backend.dao.SaveRecipeRepository;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.entity.SavedRecipeEntity;
import com.recipefind.backend.entity.User;
import com.recipefind.backend.service.SaveRecipeService;
import com.recipefind.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveRecipeServiceImpl implements SaveRecipeService {

    private final UserService userService;
    private final SaveRecipeRepository saveRecipeRepository;
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
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SavedRecipeEntity> findByUser(User user) {
        return saveRecipeRepository.findByUser(user);
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
            return deletedRows > 0; // Returns true if the delete was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
