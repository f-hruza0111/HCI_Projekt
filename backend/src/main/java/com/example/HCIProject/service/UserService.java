package com.example.HCIProject.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.HCIProject.entity.AppUser;
import com.example.HCIProject.records.*;
import com.example.HCIProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long registerUser(RegistrationRequest request) {
        AppUser newAppUser = AppUser.builder()
                .email(request.email())
                .username(request.username())
                .password(BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, request.password().toCharArray()))
                .build();

        return  userRepository.save(newAppUser).getId();
    }

    public void follow(Long creatorID, Long userID) {
        AppUser creator = userRepository.findById(creatorID).orElseThrow(() -> new IllegalStateException("Creator not found"));
        AppUser appUser =  userRepository.findById(userID).orElseThrow(() -> new IllegalStateException("User not found"));


        List<AppUser> following = appUser.getFollowing();
        if(following.contains(creator)){
            following.remove(creator);
        } else {
            appUser.getFollowing().add(creator);
        }
        userRepository.save(appUser);
    }

    public ProfileResponse getProfile(Long id, Long userID) {
        AppUser profile = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User not found"));
        return new ProfileResponse(
                profile.getId(),
                profile.getUsername(),
                profile.getPosts().stream()
                        .map(p ->  new PostsResponse(
                                p.getId(),
                                p.getTitle(),
                                p.getContent(),
                                (long) p.getLikes().size(),
                                p.getCreatedOn().toString(),
                                p.getLastEdited() == null ? "" : p.getLastEdited().toString(),
                                p.getCreator().getUsername(),
                                p.getCreator().getId(),
                                p.getPictureFileName(),
                                userID != null && p.getLikes().contains(userRepository.findById(userID).orElseThrow(() -> new IllegalStateException("User with id " + userID + " not found"))),
                                p.getComments().stream()
                                        .map(comment -> new CommentResponse(
                                                comment.getId(),
                                                comment.getContent(),
                                                comment.getLikes().size(),
                                                new UserResponse(comment.getCreator().getId(), comment.getCreator().getUsername()),
                                                comment.getCreatedOn().toString(),
                                                comment.getLastEdited() == null ? "" : comment.getLastEdited().toString(),
                                                userID != null && comment.getLikes().contains(userRepository.findById(userID).orElseThrow(() -> new IllegalStateException("User with id " + userID + " not found")))
                                        ))
                                        .toList()
                        )).collect(Collectors.toList()),
                userID != null && userRepository.findById(userID).orElseThrow(() -> new IllegalStateException("User with id " + userID + " not found")).getFollowing().contains(profile)

        );
    }

    public Long verifyUser(LoginRequest request){
        try{
            AppUser user = userRepository.findByUsername(request.username()).get();
            if(BCrypt.verifyer().verify(request.password().toCharArray(), user.getPassword().toCharArray()).verified){
                return user.getId();
            }
        } catch (NoSuchElementException e){

        }

        return -1L;
    }

    public List<UserResponse> getAllUsers(){
        List<AppUser> users = userRepository.findAll();

       return users.stream()
               .map(user -> new UserResponse(user.getId(), user.getUsername()))
               .collect(Collectors.toList());
    }

    public List<UserResponse> findUsersByUsername(String searchKey) {
        List<AppUser> users = userRepository.findByUsernameContainingIgnoreCase(searchKey);

        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }
}
