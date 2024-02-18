package com.microblog.api.blog.controller;

import com.microblog.api.blog.dto.request.CreateBlogRequest;
import com.microblog.api.blog.dto.response.BlogResponse;
import com.microblog.api.blog.service.AuthenticationService;
import com.microblog.api.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/blog")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService service;

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @PostMapping("")
    public ResponseEntity<BlogResponse> create(
            @RequestBody CreateBlogRequest request
    ) {
        try {
            return ResponseEntity.ok(service.create(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
