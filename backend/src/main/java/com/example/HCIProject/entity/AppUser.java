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
public class AppUser {
    @Id
    @GeneratedValue
    private Long id;


    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy = "creator")
    private List<Post> posts;

    @ManyToMany
    private List<AppUser> following;

    public AppUser(Long id, String email, String username, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.posts = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public AppUser(String email, String username, String password, List<Post> posts, List<AppUser> following) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.posts = posts;
        this.following = following;
    }


}
