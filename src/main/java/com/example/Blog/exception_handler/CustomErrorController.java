package com.example.Blog.exception_handler;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        int status = (int) request.getAttribute("jakarta.servlet.error.status_code");
        int s = response.getStatus();
        if (status == 401) {
            return "redirect:/access-denied";
        } else if (status == 404) {
            return "redirect:/not-found";
        } else if (status == 500) {
            return "redirect:/server-error";
        } else if (status == 400){
            return "redirect:/bad-request";
        }
        return "error";
    }
}