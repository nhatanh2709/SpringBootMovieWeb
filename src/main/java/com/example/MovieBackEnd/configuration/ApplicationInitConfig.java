package com.example.MovieBackEnd.configuration;

import com.example.MovieBackEnd.enums.Role;
import com.example.MovieBackEnd.models.User;
import com.example.MovieBackEnd.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationInitConfig.class);


    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);;
        return args -> {
            if(userRepository.findByUserName("admin") == null)
            {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                User user = User.builder().
                        userName("admin").
                        email("admin@gmail.com").
                        roles(roles).
                        passWord(passwordEncoder.encode("admin")).
                        build();
                userRepository.save(user);
                log.warn("admin user has been created with default password admin");
            }

        };
    }
}
