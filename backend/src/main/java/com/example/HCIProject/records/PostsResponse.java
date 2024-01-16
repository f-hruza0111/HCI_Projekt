package com.example.HCIProject.records;

import com.example.HCIProject.entity.AppUser;


public record PostsResponse(
        Long id,
        String title,
        String content,
        Long likes,
        String createdOn,
        String editedOn,

        String creatorUsername,
        Long creatorID
)  {



}
