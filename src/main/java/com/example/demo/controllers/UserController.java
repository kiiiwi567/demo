package com.example.demo.controllers;
import com.example.demo.config.JwtService;
import com.example.demo.models.dtos.UserInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {

    private final JwtService jwtService;

    @GetMapping("/userInfo")
    public ResponseEntity<UserInfoDTO> getUserInfo(HttpServletRequest request){
        return ResponseEntity.ok().body(jwtService.getUserRoleAndEmail(request));
    }
}
