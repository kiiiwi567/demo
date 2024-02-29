package com.example.demo.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

//    @PostMapping(value="/register")
//    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request)
//    {
//        return ResponseEntity.ok(service.register(request));
//    }


    @PostMapping(value = "/authenticate", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public RedirectView authenticate(@RequestParam("userEmail") String email,
                                     @RequestParam("password") String password,
                                     HttpServletResponse response) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail(email);
        request.setPassword(password);

        try{
        AuthenticationResponse authenticationResponse = service.authenticate(request);
        Cookie cookie = new Cookie("jwtToken", authenticationResponse.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(1000 * 60 * 60 * 24);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return new RedirectView("/allTickets");
        } catch (UsernameNotFoundException e) {
            return new RedirectView("/login?showEmailWarning=true");
        } catch (BadCredentialsException e) {
            return new RedirectView("/login?showPasswordWarning=true");
        }
    }

    @PostMapping(value="/logout")
    public RedirectView logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return new RedirectView("/login");
    }
}
