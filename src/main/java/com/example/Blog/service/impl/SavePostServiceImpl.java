package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.dto.input_dto.ToggleDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.ErrorCode;
import com.example.Blog.enums.SuccessCode;
import com.example.Blog.model.Post;
import com.example.Blog.model.SavedPost;
import com.example.Blog.model.User;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.repository.SavePostRepository;
import com.example.Blog.service.SavePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SavePostServiceImpl implements SavePostService {


    @Autowired
    private SavePostRepository savePostRepository;

    @Autowired
    private PostRepository postRepository;


    @Override
    public Response<Object> toggleSavePostForUser(Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        User currentUser = AuthProvider.getAuthenticatedUser();
        ToggleDto dto = new ToggleDto();
        SuccessCode code;
        if (Objects.nonNull(currentUser)) {
            if (post.isPresent()) {
                Optional<SavedPost> existing = savePostRepository.findByPostAndUser(post.get(), currentUser);
                if (existing.isPresent()) {
                    savePostRepository.deleteById(existing.get().getId());
                    dto.setToggled(Boolean.FALSE);
                    code = SuccessCode.REMOVED_FROM_LIBRARY;
                } else {
                    SavedPost savedPost = new SavedPost();
                    savedPost.setPost(post.get());
                    savedPost.setUser(currentUser);
                    savePostRepository.save(savedPost);
                    dto.setToggled(Boolean.TRUE);
                    code = SuccessCode.SAVED_TO_LIBRARY;
                }
                return new Response<>(dto, code);
            }
            return new Response<>(ErrorCode.INVALID_POST);
        }
        return new Response<>(ErrorCode.LOGIN_REQUIRED);
    }

    @Override
    public Page<Post> getSavedPostForUser(SearchDto searchDto) {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            int start = searchDto.getOffset();
            int limit = searchDto.getLimit();
            String sortField = searchDto.getSortByField();
            Sort.Direction sortDirection = searchDto.getOrderBy().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(start - 1, limit, Sort.by(sortDirection, sortField));
            return savePostRepository.findAllPostByUser(user, pageable);
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public List<Long> getSavedPostIdForUser() {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.nonNull(user)) {
            return savePostRepository.findAllPostIdByUser(user.getId());
        }
        return Collections.emptyList();
    }
}
