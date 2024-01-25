package com.example.HCIProject.records;

import com.example.HCIProject.entity.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.util.List;

public record CommentResponse(
         Long id,


         String content,


         Integer likes,


         UserResponse creator,

         String createdOn,

         String lastEdited
) {

}
