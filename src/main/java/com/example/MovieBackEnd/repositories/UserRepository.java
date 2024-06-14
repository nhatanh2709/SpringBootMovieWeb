package com.example.MovieBackEnd.repositories;

import com.example.MovieBackEnd.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    @Query("{ 'email': ?0 }")
    User findByEmail(String email);

    @Query("{ 'userName': ?0 }")
    User findByUserName(String userName);
}
