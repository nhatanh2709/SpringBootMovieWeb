package com.example.MovieBackEnd.controllers;

import com.example.MovieBackEnd.Service.AuthenticationService;
import com.example.MovieBackEnd.Service.UserServices;
import com.example.MovieBackEnd.dto.request.ApiResponse;
import com.example.MovieBackEnd.dto.response.UserResponse;
import com.example.MovieBackEnd.models.User;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/users")
public class UserControllers {

    private static final Logger log = LoggerFactory.getLogger(UserControllers.class);
    @Autowired
    private UserServices services;
    @RequestMapping(value="/register", method=RequestMethod.POST, produces = {"application/JSON"})
    private ApiResponse<UserResponse> PostUser(@RequestBody @Valid User Request) throws Exception {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(services.createUser(Request));
        return apiResponse;
    }

    @RequestMapping(value="/getAll", method = RequestMethod.GET, produces = {"application/JSON"})
    private ApiResponse<List<User> >getUsers() {
        var authenticate = SecurityContextHolder.getContext().getAuthentication();
        authenticate.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<User>>builder().result(services.getAllUsers()).build();
    }

    @RequestMapping(value="/myInfo" , method = RequestMethod.GET, produces = {"application/JSON"})
    private ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder().result(services.getMyInfo()).build();
    }

    @RequestMapping(value="/update/{userID}", method = RequestMethod.PUT,produces = {"application/JSON"})
    private ApiResponse<UserResponse> updateUser(@PathVariable("userID") String UserID, @RequestBody User Request)throws Exception
    {
        return ApiResponse.<UserResponse>builder().result(services.updateUser(UserID,Request)).build();
    }

    @RequestMapping(value="/delete/{userID}", method = RequestMethod.DELETE, produces = {"application/JSON"})
    private ApiResponse<String> deleteUser(@PathVariable("userID") String UserID, @RequestBody User Request)
    {
        services.deleteUser(UserID);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @RequestMapping(value="/isAdmin", method = RequestMethod.GET, produces = {"application/JSON"})
    private ApiResponse<Boolean> isAdmin() {
        var authenticate = SecurityContextHolder.getContext().getAuthentication();
        authenticate.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        var result = services.isAdmin();
        return ApiResponse.<Boolean>builder().result(result).build();
    }

}
