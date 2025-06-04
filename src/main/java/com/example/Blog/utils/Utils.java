package com.example.Blog.utils;

import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.input_dto.CommentDto;
import com.example.Blog.model.Comment;
import com.example.Blog.model.User;
import com.example.Blog.service.MinioService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Utils {

    @Autowired
    private MinioService minioService;

    public List<CommentDto> mapToDto(List<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDto dto = new CommentDto();
            dto.setCommenter(new UserDto());
            if (Objects.isNull(comment.getCommenter())) {
                comment.setCommenter(new User());
            }
            String key = comment.getCommenter().getImageKey();
            BeanUtils.copyProperties(comment, dto);
            BeanUtils.copyProperties(comment.getCommenter(), dto.getCommenter());
            if (StringUtils.hasText(key) && !comment.getName().equals("anonymousUser")) {
                dto.getCommenter().setImageUrl(minioService.downloadFileBase64(key));
            }
            dtos.add(dto);
        }
        return dtos;
    }
}
