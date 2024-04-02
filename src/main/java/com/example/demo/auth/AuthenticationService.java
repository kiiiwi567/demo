package com.example.demo.auth;

import com.example.demo.config.JwtService;
import com.example.demo.models.entities.User;
import com.example.demo.models.enums.UserRole;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.Employee)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    public String authenticate(String email, String password, HttpServletResponse response){
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail(email);
        request.setPassword(password);

        try{
            AuthenticationResponse authenticationResponse = authenticateRequest(request);
            Cookie cookie = new Cookie("jwtToken", authenticationResponse.getToken());
            cookie.setPath("/");
            cookie.setMaxAge(1000 * 60 * 60 * 24);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return "/allTickets";
        } catch (UsernameNotFoundException e) {
            return "/login?showEmailWarning=true";
        } catch (BadCredentialsException e) {
            return "/login?showPasswordWarning=true";
        }
    }

    public AuthenticationResponse authenticateRequest(AuthenticationRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } else {
            System.out.println("Authentication failed: Passwords do not match");
            throw new BadCredentialsException("Bad credentials");
        }
    }
    public void clearToken(HttpServletResponse response){
        Cookie cookie = new Cookie("jwtToken", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
