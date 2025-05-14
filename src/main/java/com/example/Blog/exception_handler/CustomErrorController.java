package com.example.Blog.exception_handler;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        int status = (int) request.getAttribute("javax.servlet.error.status_code");
        if (status == 401) {
            return "redirect:/access-denied";
        } else if (status == 404) {
            return "redirect:/not-found";
        } else if (status == 500) {
            return "redirect:/server-error";
        }
        return "error";
    }
}