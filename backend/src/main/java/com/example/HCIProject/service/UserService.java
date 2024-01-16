package com.example.HCIProject.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.HCIProject.entity.AppUser;
import com.example.HCIProject.records.LoginRequest;
import com.example.HCIProject.records.ProfileResponse;
import com.example.HCIProject.records.RegistrationRequest;
import com.example.HCIProject.records.UserResponse;
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

        appUser.getFollowing().add(creator);
        userRepository.save(appUser);
    }

    public ProfileResponse getProfile(Long id) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User not found"));
        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getPosts()
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
