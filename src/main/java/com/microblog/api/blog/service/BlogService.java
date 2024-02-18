package com.microblog.api.blog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microblog.api.blog.dto.request.AuthenticationRequest;
import com.microblog.api.blog.dto.request.CreateBlogRequest;
import com.microblog.api.blog.dto.request.RegisterRequest;
import com.microblog.api.blog.dto.response.AuthenticationResponse;
import com.microblog.api.blog.dto.response.BlogResponse;
import com.microblog.api.blog.model.Blog;
import com.microblog.api.blog.model.User;
import com.microblog.api.blog.repository.BlogRepository;
import com.microblog.api.blog.repository.TokenRepository;
import com.microblog.api.blog.repository.UserRepository;
import com.microblog.api.blog.security.Role;
import com.microblog.api.blog.security.token.Token;
import com.microblog.api.blog.security.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository repository;
    private final AuthenticationService authenticationService;

    // Create a blog
    public BlogResponse create(CreateBlogRequest request) throws Exception {
        try {
            User user = authenticationService.getCurrentAuthUser();

            Blog blog = new Blog();
            blog.setTitle(request.getTitle());
            blog.setUser(user);

            Blog blogCreated = repository.save(blog);

            return BlogResponse.builder().title(blogCreated.getTitle()).build();
        } catch (Exception e) {
            throw new Exception("Could not create the blog", e);
        }
    }
}