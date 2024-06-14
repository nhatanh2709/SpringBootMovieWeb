package com.example.MovieBackEnd.Service;

import com.example.MovieBackEnd.models.User;
import com.example.MovieBackEnd.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserQuery {

    public User findByEmail(UserRepository user, User ReqUser) {
        User newUser = user.findByEmail(ReqUser.getEmail());
        if(newUser == null || newUser.getEmail() != ReqUser.getEmail()){
            return null;
        }
        return newUser;
    }
    public User findByUserName(UserRepository user, User ReqUser) {
        User newUser = user.findByUserName(ReqUser.getUserName());
        if(newUser == null || newUser.getEmail() != ReqUser.getEmail()){
            return null;
        }
        return newUser;
    }
}
