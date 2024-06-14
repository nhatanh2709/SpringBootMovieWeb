package com.example.MovieBackEnd.Service;

import com.example.MovieBackEnd.dto.response.UserResponse;
import com.example.MovieBackEnd.enums.Role;
import com.example.MovieBackEnd.exception.AppException;
import com.example.MovieBackEnd.exception.ErrorCode;
import com.example.MovieBackEnd.models.User;
import com.example.MovieBackEnd.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServices {
    @Autowired
    private final UserRepository user;

    public User GetByEmail(@PathVariable String Email)
    {
        User existUser = user.findByEmail(Email);
        return existUser;
    }

    public User GetByUserName(@PathVariable String UserName)
    {
        User existUser = user.findByUserName(UserName);
        return existUser;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return user.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean isAdmin() {
        return true;
    }
    @PostAuthorize("returnObject.email == authentication.name")
    public User getUser(String id) {
        return user.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponse formatUserResponse(User newUser)
    {
        UserResponse response = new UserResponse();
        response.setUserName(newUser.getUserName());
        response.setRoles(newUser.getRoles());
        response.setEmail(newUser.getEmail());
        response.setBuyPackage(newUser.getBuyPackage());
        response.setExpireDate(newUser.getExprieDate());
        response.setProfilePic(newUser.getProfilePic());
        response.set_id(newUser.get_id());
        return response;
    }
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User ans = user.findByEmail(email);
        if(ans == null)
        {
            throw new AppException(ErrorCode.EMAIL_NOT_EXISTED);
        }
        return formatUserResponse(ans);

    }
    public UserResponse updateUser(String userID, User Request) throws Exception{
        User newUser = getUser(userID);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Request.setPassWord(passwordEncoder.encode(Request.getPassWord()));
        newUser.setPassWord(Request.getPassWord());
        newUser.setBuyPackage(Request.getBuyPackage());
        newUser.setProfilePic(Request.getProfilePic());
        newUser.setExprieDate(Request.getExprieDate());
        return formatUserResponse(user.save(newUser));
    }
    public UserResponse createUser(User Request) throws Exception{
        if(Request.getEmail() == null){
            throw new AppException(ErrorCode.EMAIL_NULL);
        }
        if(Request.getUserName() == null){
            throw new AppException(ErrorCode.USERNAME_NULL);
        }
        if(Request.getPassWord() == null){
            throw new AppException(ErrorCode.PASSWORD_NULL);
        }
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        Request.setRoles(roles);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);;
        Request.setPassWord(passwordEncoder.encode(Request.getPassWord()));
        UserResponse newUser = save(Request);
        return newUser;

    }

    public UserResponse save(User ReqUser) {
        User ExistEmail = user.findByEmail(ReqUser.getEmail());
        User ExistUserName = user.findByUserName(ReqUser.getUserName());
        if(ExistEmail != null){
            throw new AppException(ErrorCode.EMAIL_EXITED);
        }

        if(ExistUserName !=null){
            throw  new AppException(ErrorCode.USER_EXISTED);
        }

        User newUser = user.save(ReqUser);
        return formatUserResponse(newUser);
    }

    public void deleteUser(String userID){
        user.deleteById(userID);
    }
}
