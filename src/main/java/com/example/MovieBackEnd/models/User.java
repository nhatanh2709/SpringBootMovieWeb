package com.example.MovieBackEnd.models;

import com.mongodb.lang.NonNull;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Document(collection = "User")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String _id;

    @Indexed(unique = true)
    @Size(min = 3, message = "USERNAME_INVALID")
    private String userName;

    @Indexed(unique = true)
    private String email;

    @Size(min = 6, message = "PASSWORD_INVALID")
    private String passWord;
    @Builder.Default
    private String profilePic = "";
    Set<String>roles;
    @Builder.Default
    private Boolean verify = false;
    @Builder.Default
    private Boolean buyPackage = false;
    @Builder.Default
    private String exprieDate = "";

}
