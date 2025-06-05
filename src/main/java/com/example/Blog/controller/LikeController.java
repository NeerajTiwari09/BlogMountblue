package com.example.Blog.controller;

import com.example.Blog.constant.ToastConstant;
import com.example.Blog.dto.UserDto;
import com.example.Blog.dto.input_dto.CommentDto;
import com.example.Blog.dto.input_dto.LikeDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Comment;
import com.example.Blog.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;


    @PostMapping
    @ResponseBody
    public Response<LikeDto> likePost(@RequestParam Integer postId) {
        return likeService.toggleLike(postId);
    }


    @PostMapping("/liked-by")
    public String getLazyLikes(@RequestBody SearchDto searchDto, Model model) {
        Response<List<UserDto>> output = likeService.getLazyLikes(searchDto);
        if(!output.isSuccess()) {
            model.addAttribute(ToastConstant.TOAST_MESSAGE, output.getMessage());
            return "fragments/empty :: emptyFragment";
        }
        if(output.getData().isEmpty()) {
            model.addAttribute(ToastConstant.TOAST_MESSAGE, "End of the Likes.");
            return "fragments/empty :: emptyFragment";
        }
        model.addAttribute("users", output.getData());
        return "fragments/userList :: userListFragment";
    }
}
