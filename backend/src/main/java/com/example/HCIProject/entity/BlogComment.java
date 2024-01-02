package com.example.HCIProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogComment {

    @Id
    @GeneratedValue
   private Long id;

    @Column(length = 5000)
    private String content;

    @OneToMany
    private List<AppUser> likes;

    @ManyToOne
    private AppUser creator;

    private LocalDate createdOn;

    private LocalDate lastEdited;

    public BlogComment(String content, List<AppUser> likes, AppUser creator) {
        this.content = content;
        this.likes = likes;
        this.creator = creator;
    }
}
