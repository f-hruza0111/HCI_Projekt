package com.example.HCIProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 5000)
    private String blogPost;

//    private List<Byte[]> images;

    @ManyToOne
    private AppUser creator;

    @OneToMany
    private List<BlogComment> comments;

    @OneToMany
    private List<AppUser> likes;

    private LocalDate createdOn;
    private LocalDate lastEdited;

    public Post(String blogPost, List<Byte[]> images, AppUser creator, LocalDate createdOn, LocalDate lastEdited) {
        this.blogPost = blogPost;
//        this.images = images;
        this.creator = creator;
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.createdOn = createdOn;
    }
}
