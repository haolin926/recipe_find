package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "comments", schema = "recipe_db", catalog = "recipe_application")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "recipe_id")
    private Integer recipeId;

    @Column(name = "comment_content")
    private String commentContent;

    @Column(name = "rate")
    private Float rate;

    @ElementCollection
    @CollectionTable(name = "comment_images", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "image", columnDefinition = "TEXT")
    private List<String> images;

}
