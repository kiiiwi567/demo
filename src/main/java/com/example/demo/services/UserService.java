package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

        public Optional<User> getUserByPrincipal(Principal principal){
            if (principal == null) {
                return Optional.of(new User());
            }
            return userRepository.findByEmail(principal.getName());
        }
}
