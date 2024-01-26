package com.example.HCIProject.records;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreatePostRequest (
        Long creatorID,

        Long postID,
        String content,
        String title,
        String pictureFormat

){}
