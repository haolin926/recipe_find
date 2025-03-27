package com.recipefind.backend.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefind.backend.dao.CommentRepository;
import com.recipefind.backend.entity.*;
import com.recipefind.backend.service.CommentService;
import com.recipefind.backend.service.RecipeService;
import com.recipefind.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final RecipeService recipeService;
    private final UserService userService;

    @Override
    @Transactional
    public Integer saveComment(CommentDTO commentDTO) throws JsonProcessingException {
        Recipe savedRecipe = recipeService.findRecipeByApiId(commentDTO.getRecipeApiId());
        User user = userService.getUserById(commentDTO.getUserId());

        if (user == null) {
            throw new RuntimeException("No such user exist");
        }

        if (savedRecipe == null) {
            RecipeDTO recipeDTO = recipeService.findRecipeInSpoonacularByApiId(commentDTO.getRecipeApiId());
            savedRecipe = recipeService.saveRecipe(recipeDTO);
        }

        Long recipeId = savedRecipe.getRecipeId();

        Comment comment = new Comment();

        comment.setUserId(commentDTO.getUserId());
        comment.setCommentContent(commentDTO.getComment());
        comment.setImages(commentDTO.getImages());
        comment.setRecipeId(recipeId.intValue());
        comment.setRate(commentDTO.getRate());

        try {
            Comment savedComment = commentRepository.save(comment);

            if (savedComment.getCommentId() != null) {

                if (commentDTO.getRate() != 0.0) {
                    recipeService.updateRecipeRate(recipeId, commentDTO.getRate());
                }

                return savedComment.getCommentId();
            } else {
                throw new RuntimeException("Failed to save comment!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CommentDTO> getCommentsForRecipe(Integer recipeApiId) {
        Recipe recipe = recipeService.findRecipeByApiId(recipeApiId);
        Long recipeId;
        if (recipe == null) {
            return new ArrayList<>();

        } else {
            recipeId = recipe.getRecipeId();

        }

        List<Comment> comments = commentRepository.getCommentsByRecipeId(recipeId.intValue());
        List<CommentDTO> commentDTOList = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDTO commentDTO = new CommentDTO();

            commentDTO.setComment(comment.getCommentContent());
            commentDTO.setImages(comment.getImages());
            commentDTO.setRate(comment.getRate());

            User user = userService.getUserById(comment.getUserId());

            commentDTO.setUserName(user.getUsername());
            commentDTO.setUserPhoto(user.getUserPhoto());

            commentDTOList.add(commentDTO);
        }

        return commentDTOList;
    }
}
