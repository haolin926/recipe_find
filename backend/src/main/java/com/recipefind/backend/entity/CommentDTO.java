package com.recipefind.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Integer userId;
    private Integer recipeApiId;
    private String comment;
    private List<String> images;
    private Float rate;
    private String userName;
    private String userPhoto;
}

