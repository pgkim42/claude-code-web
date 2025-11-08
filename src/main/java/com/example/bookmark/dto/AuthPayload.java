package com.example.bookmark.dto;

import com.example.bookmark.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthPayload {
    private User user;
    private String message;
}
