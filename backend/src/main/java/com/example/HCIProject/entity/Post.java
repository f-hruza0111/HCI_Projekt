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
public class Post implements Comparable{

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 5000)
    private String content;
    private String title;

//    private File image;

    @ManyToOne
    private AppUser creator;

    @OneToMany
    private List<BlogComment> comments;

    @OneToMany
    private List<AppUser> likes;

    private LocalDate createdOn;
    private LocalDate lastEdited;
    private String pictureFileName;

    public Post(String title, String content, /*List<Byte[]> images,*/ AppUser creator, LocalDate createdOn, LocalDate lastEdited, String pictureExtension) {
        this.content = content;
//        this.images = images;
        this.creator = creator;
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.createdOn = createdOn;
        this.pictureFileName = pictureExtension;
    }

    @Override
    public int compareTo(Object o) {
        if(!this.getClass().equals(o.getClass())) return -1;

        Post other = (Post) o;
        return this.createdOn.compareTo(other.createdOn);
    }
}
