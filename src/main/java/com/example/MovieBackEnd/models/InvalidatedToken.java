package com.example.MovieBackEnd.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class InvalidatedToken {
    @Id
    private String id;
    private Date expiryTime;
}
