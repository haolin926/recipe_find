package com.recipefind.backend.controller;


import com.recipefind.backend.entity.CommentDTO;
import com.recipefind.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @PostMapping("/save")
    public ResponseEntity<?> postComment(@RequestBody CommentDTO commentDTO) {
        try {
            Integer savedCommentId = commentService.saveComment(commentDTO);

            if (savedCommentId != null) {
                return ResponseEntity.ok(savedCommentId);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save comment");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save comment");
        }
    }

    @GetMapping
    public ResponseEntity<?> getCommentForRecipe (@RequestParam("recipeId") Integer recipeApiId) {
        try {
            List<CommentDTO> commentDTOS = commentService.getCommentsForRecipe(recipeApiId);

            return ResponseEntity.ok(commentDTOS);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get comments for current recipe");
        }
    }
}
