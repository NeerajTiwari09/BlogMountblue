package com.example.Blog.controller;

import com.example.Blog.dto.input_dto.PostDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Post;
import com.example.Blog.model.Tag;
import com.example.Blog.service.PostService;
import com.example.Blog.service.impl.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostApiController {

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @PostMapping("/post")
    public Response<Object> getPosts(@RequestBody SearchDto searchDto) {
        Page<Post> posts = postService.findByFilterCriteria(searchDto);
        return new Response<>(posts);
    }

    @PostMapping("/post/publish")
    public Response<Object> savePost(@RequestBody PostDto postDto) {
        List<Tag> tags = tagService.findTagIds(postDto);
        postDto.setTags(new HashSet<>(tags));
        Post post = postService.publishPost(postDto);
        return new Response<>(post);
    }
}
