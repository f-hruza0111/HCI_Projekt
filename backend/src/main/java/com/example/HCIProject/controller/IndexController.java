package com.example.HCIProject.controller;

import com.example.HCIProject.entity.AppUser;
import com.example.HCIProject.entity.Post;
import com.example.HCIProject.records.*;
import com.example.HCIProject.service.PostsService;
import com.example.HCIProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
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
    List<Post> getAllPosts(){

        //todo paging i sortiranje po followerima


        return postsService.getAllPosts();
    }

    @PostMapping(path = "/post")
    void createPost( @RequestParam("image") MultipartFile image) throws IOException {
        System.out.println(image.getName());
//        postsService.createPost(request, image);
    }

    @PutMapping("/post")
    void editPost(@RequestParam Long postID, @RequestBody CreatePostRequest postRequest, @RequestPart MultipartFile image){
        postsService.editPost(postRequest, postID);
    }

    /**
     * Radi i daje error za duplikate
     * @param request
     * @return
     */
    @PostMapping("/registration")
    ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request){
        return ResponseEntity.created(URI.create(String.valueOf(userService.registerUser(request)))).build();
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


    @GetMapping("/profile/{id}")
    ResponseEntity<ProfileResponse> getProfile(@PathVariable Long id){
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @GetMapping("/users")
    List<AppUser> getAllUsers(){
        return userService.getAllUsers();
    }
}
