package com.example.MovieBackEnd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String _id;
    private String userName;
    private String email;
    private String profilePic;
    Set<String> roles;
    private Boolean buyPackage;
    private String expireDate;
}
