package com.example.bookmark.dto;

import lombok.Data;

/**
 * Input DTO for creating a new tag.
 * Maps to GraphQL CreateTagInput type.
 */
@Data
public class CreateTagInput {
    private String name;
    private String color;
}
