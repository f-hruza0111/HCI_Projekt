package com.example.HCIProject.service;

import com.example.HCIProject.entity.User;
import com.example.HCIProject.records.RegistrationRequest;
import com.example.HCIProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void registerUser(RegistrationRequest request) {
        User newUser = User.builder()
                .username(request.username())
                .password(request.password())
                .build();

        userRepository.save(newUser);
    }

    public void follow(Long creatorID, Long userID) {
        User creator = userRepository.findById(creatorID).orElseThrow(() -> new IllegalStateException("Creator not found"));
        User user =  userRepository.findById(userID).orElseThrow(() -> new IllegalStateException("User not found"));

        user.getFollowing().add(creator);
        userRepository.save(user);
    }
}
