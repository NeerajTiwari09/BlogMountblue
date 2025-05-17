package com.example.Blog.controller.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/access-denied")
    public String showAccessDeniedPage() {
        return "/error/access-denied";
    }

    @RequestMapping("/not-found")
    public String showPageNotFound() {
        return "/error/page-not-found";
    }

    @RequestMapping("/server-error")
    public String showServerErrorPage() {
        return "/error/internal-server-error";
    }

    @RequestMapping("/bad-request")
    public String showBadRequestPage() {
        return "/error/bad-request";
    }
}
