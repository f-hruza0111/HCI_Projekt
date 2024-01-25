package com.example.HCIProject.records;

public record DeleteCommentRequest(
        Long postID,
        Long commentID
) {
}
