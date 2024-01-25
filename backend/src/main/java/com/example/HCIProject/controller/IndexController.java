package com.example.HCIProject.controller;

import com.example.HCIProject.records.*;
import com.example.HCIProject.service.PostsService;
import com.example.HCIProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController("/home")
@RequiredArgsConstructor
public class IndexController {
    private final PostsService postsService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody LoginRequest request){
        Long id = userService.verifyUser(request);
        HttpStatus status = id == -1 ? HttpStatus.FORBIDDEN : HttpStatus.OK;
       return new ResponseEntity<>(id, status);
    }

    @GetMapping("/posts")
    List<PostsResponse> getAllPosts(@RequestParam(required = false) Long userID){

        //todo paging(ovo ako stignemo) i sortiranje po followerima


        return postsService.getAllPosts(userID);
    }

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request){
//        System.out.println(image.getName());
        return ResponseEntity.created(URI.create(String.valueOf(postsService.createPost(request)))).build();
    }

    @PutMapping("/post/{postID}")
    public void editPost(@PathVariable Long postID, @RequestBody CreatePostRequest postRequest){
        postsService.editPost(postRequest, postID);
    }

    @DeleteMapping("/post/{postID}")
        public void deletePost(@PathVariable Long postID){
            postsService.deletePost(postID);
        }


    /**
     * Radi i daje error za duplikate
     * @param request
     * @return
     */
    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request){
        return ResponseEntity.created(URI.create(String.valueOf(userService.registerUser(request)))).build();
    }

    @PostMapping("/comment")
    public void postComment(PostCommentRequest request){
        postsService.addComment(request);
    }
    @PutMapping("/comment")
    public void editComment(@RequestParam Long commentID, PostCommentRequest request){
        postsService.editComment(request, commentID);
    }

    @DeleteMapping("/comment")
    public void deleteComment(@RequestBody DeleteCommentRequest request){
        postsService.deleteComment(request);
    }

    @PostMapping("/post/like")
    public void likePost(LikeRequest request){
        postsService.likePost(request);
    }

    @PostMapping("/comment/like")
    public void likeComment(LikeRequest request){
        postsService.likeComment(request);
    }

    @PostMapping("/follow")
    public void followCreator(Long creatorID, Long userID){
        userService.follow(creatorID, userID);
    }


    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long id){
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers(@RequestParam(defaultValue = "") String searchKey){
        if(searchKey.equals("")){
            return new ArrayList<>();
        } else {
            return userService.findUsersByUsername(searchKey);
        }
    }
}
