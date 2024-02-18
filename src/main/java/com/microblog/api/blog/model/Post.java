package com.microblog.api.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blog_id", nullable = false)
    private Long blogId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition="TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    // Define the relation with Blog
    @ManyToOne
    @JoinColumn(name="blog_id", insertable = false, updatable = false)
    private Blog blog;

    // getters and setters
}

