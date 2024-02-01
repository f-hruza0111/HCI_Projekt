package com.example.HCIProject.records;

import com.example.HCIProject.entity.AppUser;
import com.example.HCIProject.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;

import java.util.List;

public record ProfileResponse(

       Long id,

       String username,
       List<PostsResponse> posts,

       boolean followed

) {
}
