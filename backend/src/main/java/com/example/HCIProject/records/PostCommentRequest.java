package com.example.HCIProject.records;

public record PostCommentRequest(
        String comment,

        Long postID,
        Long creatorID
) {


}
