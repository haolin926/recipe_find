package com.recipefind.backend.dao;

import com.recipefind.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> getCommentsByRecipeId(Integer recipeId);

    Integer countCommentsByRecipeIdAndRateNot(Integer recipeId, Float rate);
}
