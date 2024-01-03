package com.example.HCIProject.service;

import com.example.HCIProject.entity.AppUser;
import com.example.HCIProject.records.RegistrationRequest;
import com.example.HCIProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long registerUser(RegistrationRequest request) {
        AppUser newAppUser = AppUser.builder()
                .email(request.email())
                .username(request.username())
                .password(request.password())
                .build();

        return  userRepository.save(newAppUser).getId();
    }

    public void follow(Long creatorID, Long userID) {
        AppUser creator = userRepository.findById(creatorID).orElseThrow(() -> new IllegalStateException("Creator not found"));
        AppUser appUser =  userRepository.findById(userID).orElseThrow(() -> new IllegalStateException("User not found"));

        appUser.getFollowing().add(creator);
        userRepository.save(appUser);
    }
}
