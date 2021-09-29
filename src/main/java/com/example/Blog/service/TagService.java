package com.example.Blog.service;

import com.example.Blog.model.Tag;
import com.example.Blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<Tag> findAll(){
        return tagRepository.findAll();
    }

    public List<Tag> findAllById(List<Integer> ids){
        return tagRepository.findAllById(ids);
    }

    public List<Tag> findTagIds(Tag tag) {
        boolean isExist = tagRepository.existsByName(tag.getName());
        if (!isExist) {
            tag.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            tag.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            tagRepository.save(tag);
        }
        return tagRepository.findAllByName(tag.getName());
    }
}
