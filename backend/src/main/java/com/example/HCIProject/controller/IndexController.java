package com.example.HCIProject.controller;

import com.example.HCIProject.entity.Post;
import com.example.HCIProject.records.*;
import com.example.HCIProject.service.PostsService;
import com.example.HCIProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/home")
@RequiredArgsConstructor
public class IndexController {
    private final PostsService postsService;
    private final UserService userService;

    @PostMapping("/login")
    public String login(LoginRequest request){
        return "Login endpoint";
    }

    @GetMapping("/posts")
    List<Post> getAllPosts(){

        //todo paging i sortiranje po followerima


        return postsService.getAllPosts();
    }

    @PostMapping("/post")
    void createPost(CreatePostRequest request){
        postsService.createPost(request);
    }

    @PutMapping("/post")
    void editPost(@RequestParam Long postID, CreatePostRequest postRequest){
        postsService.editPost(postRequest, postID);
    }

    @PostMapping("/registration")
    void registerUser(RegistrationRequest request){
        userService.registerUser(request);
    }

    @PostMapping("/comment")
    void postComment(PostCommentRequest request){
        postsService.addComment(request);
    }
    @PutMapping("/comment")
    void editComment(@RequestParam Long commentID, PostCommentRequest request){
        postsService.editComment(request, commentID);
    }

    @PostMapping("/post/like")
    void likePost(LikeRequest request){
        postsService.likePost(request);
    }

    @PostMapping("/comment/like")
    void likeComment(LikeRequest request){
        postsService.likeComment(request);
    }

    @PostMapping("/follow")
    void followCreator(Long creatorID, Long userID){
        userService.follow(creatorID, userID);
    }
}
