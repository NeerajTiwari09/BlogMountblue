package com.example.Blog.service;

import com.example.Blog.model.Post;
import com.example.Blog.model.PostTag;
import com.example.Blog.model.Tag;
import com.example.Blog.repository.PostTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PostTagService {
    @Autowired
    private PostTagRepository postTagRepository;

    public List<Integer> findAllTagIdByPostId(Integer postId) {
        return postTagRepository.findAllByPostId(postId);
    }

    public void saveTagId(List<Tag> tags, Post post) {
        List<PostTag> postTags = new ArrayList<>();
        for (Tag tag : tags) {
            PostTag postTag = new PostTag();
            postTag.setPostId(post.getId());
            postTag.setTagId(tag.getId());
            postTag.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            postTag.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            postTags.add(postTag);
        }
        postTagRepository.saveAll(postTags);
    }

    public Set<Integer> findAllPostIdByTagId(List<Integer> tagId) {

        return postTagRepository.findPostIdByTagId(tagId);
    }

    public Set<Integer> findPostIdsByTagName(String searchString) {
        return postTagRepository.findAllPostIdByTagName(searchString);
    }
}
