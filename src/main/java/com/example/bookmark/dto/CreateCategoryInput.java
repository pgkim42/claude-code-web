package com.example.bookmark.dto;

import lombok.Data;

/**
 * Input DTO for creating a new category.
 * Maps to GraphQL CreateCategoryInput type.
 */
@Data
public class CreateCategoryInput {
    private String name;
    private String description;
}
