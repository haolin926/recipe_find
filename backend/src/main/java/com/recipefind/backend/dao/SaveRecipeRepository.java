package com.recipefind.backend.dao;

import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.entity.SavedRecipeEntity;
import com.recipefind.backend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaveRecipeRepository extends JpaRepository<SavedRecipeEntity, Integer> {
    List<SavedRecipeEntity> findByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM SavedRecipeEntity s WHERE s.user.id = :userId AND s.recipe.recipeId = :recipeId")
    int deleteByUserIdAndRecipeId(@Param("userId") Integer userId, @Param("recipeId") Integer recipeId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SavedRecipeEntity s WHERE s.user = :user AND s.recipe = :recipe")
    boolean existsByUserAndRecipe(@Param("user") User user, @Param("recipe") Recipe recipe);
}
