package com.example.HCIProject.records;

import com.example.HCIProject.entity.AppUser;

import java.util.List;


public record PostsResponse(
        Long id,
        String title,
        String content,
        Long likes,
        String createdOn,
        String editedOn,

        String creatorUsername,
        Long creatorID,

        List<CommentResponse> comments
)  {



}
