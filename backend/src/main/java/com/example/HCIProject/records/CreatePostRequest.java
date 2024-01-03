package com.example.HCIProject.records;

import java.util.List;

public record CreatePostRequest (
        Long creatorID,
        String content,
        String title
){}
