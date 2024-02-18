package com.microblog.api.blog.repository;

import java.util.Optional;

import com.microblog.api.blog.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Integer> {

}