package com.example.MovieBackEnd.repositories;

import com.example.MovieBackEnd.models.InvalidatedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvalidatedTokenRepository extends MongoRepository<InvalidatedToken,String> {

}
