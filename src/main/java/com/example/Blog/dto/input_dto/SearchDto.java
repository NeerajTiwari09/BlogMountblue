package com.example.Blog.dto.input_dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SearchDto {
    Map<String, Object> query = new HashMap<>();
    private int limit = 10;
    private int offset;
    private String sortByField;
    private String orderBy;
    private String searchSting;
    private String publishedAt;
    private int authorId;
    private List<Integer> tagIds;

}
