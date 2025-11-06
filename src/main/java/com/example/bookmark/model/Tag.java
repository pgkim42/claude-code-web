package com.example.bookmark.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String color; // 태그 색상 (헥스 코드)

    @ManyToMany(mappedBy = "tags")
    private Set<Bookmark> bookmarks = new HashSet<>();

    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
