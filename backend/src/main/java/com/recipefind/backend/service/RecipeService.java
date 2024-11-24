package com.recipefind.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefind.backend.entity.Recipe;
import org.springframework.web.multipart.MultipartFile;

public interface RecipeService {
    Recipe constructRecipe(String queryName) throws JsonProcessingException;

    String predictName(MultipartFile image) throws JsonProcessingException;
}
