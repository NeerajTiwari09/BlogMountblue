package com.example.Blog.service.impl;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.event.EventBuffer;
import com.example.Blog.event.EventForAuthor;
import com.example.Blog.model.Comment;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;
import com.example.Blog.repository.CommentRepository;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.repository.UserRepository;
import com.example.Blog.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventBuffer eventBuffer;

    public List<Comment> findAllByPostIdOrderCreatedAtDesc(Integer postId) {
        Sort.Direction sortDirection = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortDirection, "id"));
        return commentRepository.findAllByPostIdOrderByIdDesc(postId, pageable);
    }

    public void saveOrUpdateComment(Comment comment) {
        Comment commentToSave = new Comment();
        if (comment.getId() != null) {
            commentToSave = commentRepository.findById(comment.getId()).orElse(new Comment());
        }
        User user = AuthProvider.getAuthenticatedUser();
        String name = Objects.nonNull(user) ? user.getName() : "anonymousUser";
        commentToSave.setComment(comment.getComment());
        commentToSave.setName(name);
        commentToSave.setPostId(comment.getPostId());
        commentToSave.setCommenter(user);
        commentRepository.save(commentToSave);
        Optional<Post> post = postRepository.findById(comment.getPostId());
        if (post.isPresent() && Objects.nonNull(user) && !user.getUsername().equals(post.get().getAuthor().getUsername())) {
            eventBuffer.bufferComments(new EventForAuthor(post.get(), user, post.get().getAuthor()));
        }
    }

    public Optional<Comment> findCommentById(Integer id) {
        return commentRepository.findById(id);
    }

    public void deleteCommentById(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getLazyComment(SearchDto searchDto) {
        Map<String, Object> query = searchDto.getQuery();
        if (Objects.nonNull(query.get("postId"))) {
            Integer postId = Integer.parseInt((String) query.get("postId"));
            int offset = searchDto.getOffset();
            int limit = searchDto.getLimit();
            Sort.Direction sortDirection = Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(offset - 1, limit, Sort.by(sortDirection, "id"));
            return commentRepository.findAllByPostIdOrderByIdDesc(postId, pageable);
        }
        return new ArrayList<>();
    }
}