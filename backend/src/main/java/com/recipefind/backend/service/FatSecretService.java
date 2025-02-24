package com.recipefind.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface FatSecretService {
    public String searchRecipes (String keyword) throws JsonProcessingException;
}
