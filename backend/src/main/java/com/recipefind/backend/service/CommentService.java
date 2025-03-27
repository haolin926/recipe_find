package com.recipefind.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefind.backend.entity.CommentDTO;

import java.util.List;

public interface CommentService {
    Integer saveComment(CommentDTO commentDTO) throws JsonProcessingException;

    List<CommentDTO> getCommentsForRecipe (Integer recipeApiId);
}
