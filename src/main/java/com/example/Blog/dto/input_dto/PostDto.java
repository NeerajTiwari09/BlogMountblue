package com.example.Blog.dto.input_dto;

import com.example.Blog.model.Tag;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class PostDto {

    private int id;
    private String title;
    private String excerpt;
    private String content;
    private String author;
    private String publishedAt;
    private boolean isPublished;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private List<Integer> selectedTags;
    private Set<Tag> tags = new HashSet<>();
    private String tagString;
}
