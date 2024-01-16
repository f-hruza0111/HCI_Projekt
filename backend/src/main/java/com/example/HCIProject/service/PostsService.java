package com.example.HCIProject.service;

import com.example.HCIProject.entity.AppUser;
import com.example.HCIProject.entity.BlogComment;
import com.example.HCIProject.entity.Post;
import com.example.HCIProject.records.CreatePostRequest;
import com.example.HCIProject.records.LikeRequest;
import com.example.HCIProject.records.PostCommentRequest;
import com.example.HCIProject.records.PostsResponse;
import com.example.HCIProject.repository.BlogCommentRepository;
import com.example.HCIProject.repository.PostRepository;
import com.example.HCIProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BlogCommentRepository commentRepository;


    public List<PostsResponse> getAllPosts(Long userID){


        List<PostsResponse> response = new ArrayList<>();
        List<Post> posts = postRepository.findAll();

        if(userID != null){
            AppUser user = userRepository.findById(userID).orElseThrow(() -> new IllegalStateException("User with id " + userID + " not found"));

            List<Post> followedCreatorPosts = new java.util.ArrayList<>(posts.stream()
                    .filter((post) -> user.getFollowing().contains(post.getCreator()))
                    .toList());

            posts.removeAll(followedCreatorPosts);

            followedCreatorPosts.sort(Collections.reverseOrder());

            for(Post p : followedCreatorPosts){
                response.add(
                        new PostsResponse(
                                p.getId(),
                                p.getTitle(),
                                p.getContent(),
                                (long) p.getLikes().size(),
                                p.getCreatedOn().toString(),
                                p.getLastEdited().toString(),
                                p.getCreator().getUsername(),
                                p.getCreator().getId()
                        )
                );
            }

        }

        posts.sort(Collections.reverseOrder());

        for(Post p : posts){
            response.add(
                    new PostsResponse(
                            p.getId(),
                            p.getTitle(),
                            p.getContent(),
                            (long) p.getLikes().size(),
                            p.getCreatedOn().toString(),
                            p.getLastEdited().toString(),
                            p.getCreator().getUsername(),
                            p.getCreator().getId()
                    )
            );
        }

        return response;
    }

    public Long createPost(CreatePostRequest request) {
        AppUser creator = userRepository.findById(request.creatorID()).orElseThrow(() -> new IllegalStateException("Creator not found"));




        Post post = Post.builder()
                .creator(creator)
                .title(request.title())
                .content(request.content())
//                .images(request.images())
                .createdOn(LocalDate.now())
                .build();

        return postRepository.save(post).getId();
    }

    public void editPost(CreatePostRequest request, Long postID){
        Post post = postRepository.findById(postID).orElseThrow(() -> new IllegalStateException("Post not found"));

        post.setContent(request.content());
//        post.setImages(request.images());
        post.setLastEdited(LocalDate.now());

        postRepository.save(post);
    }

    public void addComment(PostCommentRequest request) {
        AppUser creator = userRepository.findById(request.creatorID()).orElseThrow(() -> new IllegalStateException("Creator not found"));
        Post post = postRepository.findById(request.postID()).orElseThrow(() -> new IllegalStateException("Post not found"));

        BlogComment comment = BlogComment.builder()
                .creator(creator)
                .content(request.comment())
                .createdOn(LocalDate.now())
                .build();

        post.getComments().add(comment);

        commentRepository.save(comment);
        postRepository.save(post);
    }

    public void editComment(PostCommentRequest request, Long commentID) {
        AppUser creator = userRepository.findById(request.creatorID()).orElseThrow(() -> new IllegalStateException("Creator not found"));

        BlogComment comment = commentRepository.findById(commentID).orElseThrow(() -> new IllegalStateException("Comment not found"));
        if(!comment.getCreator().equals(creator)) throw new IllegalStateException("This comment was not created by this creator");

        comment.setContent(request.comment());
        comment.setLastEdited(LocalDate.now());

        commentRepository.save(comment);


    }

    public void likePost(LikeRequest request) {
        AppUser liked = userRepository.findById(request.userID()).orElseThrow(() -> new IllegalStateException("Creator not found"));
        Post post = postRepository.findById(request.contentID()).orElseThrow(() -> new IllegalStateException("Post not found"));

        List<AppUser> likes = post.getLikes();
        if(likes.contains(liked)) {
            likes.remove(liked);
        } else {
            likes.add(liked);
        }


        postRepository.save(post);
    }

    public void likeComment(LikeRequest request) {
        AppUser liked = userRepository.findById(request.userID()).orElseThrow(() -> new IllegalStateException("Creator not found"));
        BlogComment comment = commentRepository.findById(request.contentID()).orElseThrow(() -> new IllegalStateException("Post not found"));

        comment.getLikes().add(liked);

        commentRepository.save(comment);
    }
}
