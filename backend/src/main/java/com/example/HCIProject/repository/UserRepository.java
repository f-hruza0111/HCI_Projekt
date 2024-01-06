package com.example.HCIProject.repository;

import com.example.HCIProject.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {


    Optional<AppUser> findByUsername(String username);
}
