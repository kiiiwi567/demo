package com.example.demo.auth;

import com.example.demo.config.JwtService;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<?> authenticate(AuthenticationRequest credentials, HttpServletResponse response){
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail(credentials.getEmail());
        request.setPassword(credentials.getPassword());

        try{
            String token = authenticateRequest(request);
            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setPath("/");
            cookie.setMaxAge(1000 * 60 * 60 * 24);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return ResponseEntity.ok("{\"jwtToken\": \"" + token +"\"}");
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    public String authenticateRequest(AuthenticationRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return jwtService.generateToken(user);
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
