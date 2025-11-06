package com.example.bookmark.dto;

import lombok.Data;

/**
 * Input DTO for updating an existing category.
 * Maps to GraphQL UpdateCategoryInput type.
 * All fields are optional.
 */
@Data
public class UpdateCategoryInput {
    private String name;
    private String description;
}
