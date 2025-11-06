package com.example.bookmark.dto;

import lombok.Data;

/**
 * Input DTO for updating an existing tag.
 * Maps to GraphQL UpdateTagInput type.
 * All fields are optional.
 */
@Data
public class UpdateTagInput {
    private String name;
    private String color;
}
