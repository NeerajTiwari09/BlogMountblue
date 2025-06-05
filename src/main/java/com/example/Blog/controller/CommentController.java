package com.example.Blog.controller;

import com.example.Blog.auth.AuthProvider;
import com.example.Blog.constant.ToastConstant;
import com.example.Blog.dto.input_dto.CommentDto;
import com.example.Blog.dto.input_dto.SearchDto;
import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.model.Comment;
import com.example.Blog.model.Login;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;
import com.example.Blog.service.PostService;
import com.example.Blog.service.impl.CommentService;
import com.example.Blog.utils.Utils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private Utils utils;

    @RequestMapping(value = {"", "/"})
    public String makeComment(@ModelAttribute("newComment") Comment comment) {
        commentService.saveOrUpdateComment(comment);
        return "redirect:/posts/" + comment.getPostId();
    }

    @GetMapping("/update")
    public String viewUpdateCommentPage(@RequestParam("id") Integer id, Model model) {
        User user = AuthProvider.getAuthenticatedUser();
        if (Objects.isNull(user)) {
            Login login = new Login();
            model.addAttribute("login", login);
            return "login";
        }
        Optional<Comment> comment = commentService.findCommentById(id);
        model.addAttribute("updateComment", comment.get());
        return "update-comment";
    }

    @PostMapping("/update")
    public String updateComment(@ModelAttribute("updateComment") Comment comment, RedirectAttributes redirectAttributes) {
        commentService.saveOrUpdateComment(comment);
        redirectAttributes.addFlashAttribute("toastMessage", "Comment saved successfully!");
        redirectAttributes.addFlashAttribute("toastStatusColor", "bg-success");
        return "redirect:/posts/" + comment.getPostId();
    }

    @GetMapping("/delete")
    public String deleteComment(@RequestParam("commentId") Integer commentId, @RequestParam("postId") Integer postId,
                                RedirectAttributes redirectAttributes) {
        commentService.deleteCommentById(commentId);
        redirectAttributes.addFlashAttribute("toastMessage", "Comment deleted successfully!");
        redirectAttributes.addFlashAttribute("toastStatusColor", "bg-success");
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/api")
    public String getLazyComment(@RequestBody SearchDto searchDto, Model model) {
        boolean isAuthorsPost = false;
        User user = AuthProvider.getAuthenticatedUser();
        Map<String, Object> query = searchDto.getQuery();
        if (ObjectUtils.allNotNull(user, query.get("postId"))) {
            Optional<Post> post = postService.getById(Integer.parseInt((String) query.get("postId")));
            if (post.isPresent() && post.get().getAuthor().getUsername().equals(user.getUsername())) {
                isAuthorsPost = true;
            }
        }
        List<Comment> comments = commentService.getLazyComment(searchDto);
        List<CommentDto> commentDtos = utils.mapToDto(comments);
        model.addAttribute("comments", commentDtos);
        model.addAttribute("isAuthorsPost", isAuthorsPost);
        if(commentDtos.isEmpty()){
            model.addAttribute(ToastConstant.TOAST_MESSAGE, "End of the comments");
            return "fragments/empty :: emptyFragment";
        }
        return "fragments/comment :: commentFragment";
    }
}
