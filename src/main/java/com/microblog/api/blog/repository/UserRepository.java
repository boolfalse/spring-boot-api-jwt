package com.microblog.api.blog.repository;

import java.util.Optional;

import com.microblog.api.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}