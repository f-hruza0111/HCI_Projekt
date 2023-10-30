package com.example.HCIProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;


    @Column(unique = true)
    private String username;
    private String password;

    @OneToMany(mappedBy = "creator")
    private List<Post> posts;

    @OneToMany
    private List<User> following;

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.posts = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public User(String username, String password, List<Post> posts, List<User> following) {
        this.username = username;
        this.password = password;
        this.posts = posts;
        this.following = following;
    }


}
