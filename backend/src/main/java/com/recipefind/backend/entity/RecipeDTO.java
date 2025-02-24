package com.recipefind.backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeDTO {
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("instructions")
    private List<String> instructions = new ArrayList<>();

    @JsonProperty("ingredients")
    private List<Ingredient> ingredients = new ArrayList<>();

    @JsonProperty("nutrition")
    private List<Nutrition> nutrition = new ArrayList<>();

}
