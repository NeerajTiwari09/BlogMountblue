package com.example.Blog.service.impl;

import com.example.Blog.dto.input_dto.PostDto;
import com.example.Blog.model.Tag;
import com.example.Blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<Tag> findAll(){
        return tagRepository.findAll();
    }

    public List<Tag> findTagIds(PostDto postDto) {
        List<Tag> tags = new ArrayList<>();
        if(StringUtils.hasText(postDto.getTagString())) {
            String[] tagsData = postDto.getTagString().trim().split(",\\s*");
            for (String data : tagsData) {
                Tag tag = tagRepository.findByName(data);
                if (Objects.isNull(tag)) {
                    tag = new Tag();
                    tag.setName(data);
                    tag = tagRepository.save(tag);
                }
                tags.add(tag);
            }
        }
        if(!CollectionUtils.isEmpty(postDto.getSelectedTags())) {
            tags.addAll(tagRepository.findAllById(postDto.getSelectedTags()));
        }
        return tags;
    }

    public Set<Integer> findIdByName(String search) {
        return tagRepository.findIdByName(search);
    }
}