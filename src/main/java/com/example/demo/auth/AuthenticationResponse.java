package com.example.demo.auth;

import jakarta.servlet.http.Cookie;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
}
