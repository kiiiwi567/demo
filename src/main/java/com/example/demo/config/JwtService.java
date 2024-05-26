package com.example.demo.config;

import com.example.demo.models.dtos.UserInfoDTO;
import com.example.demo.models.entities.User;
import com.example.demo.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String SECRET_KEY = "6466b560cadfaaaad8787c6a16e3f8e62b125b36daee9fc7fb6c044e4f97e183";
    private final UserRepository userRepository;
    public String extractPayloadField(String token, String fieldName, String secondSplitter){
        String[] tokenParts = token.split("\\.");
        String payload = new String(Decoders.BASE64.decode(tokenParts[1]));
        return payload.split(fieldName)[1].split(secondSplitter)[0].replaceAll("\"", "");
    }
    public String extractUsername(String token) {
        return extractPayloadField(token, "\"sub\":", ",");
    }

    public String extractRole(String token) {
        String rawRole = extractClaim(token, claims -> claims.get("role", String.class));
        return rawRole.substring(1,rawRole.length() - 1);
    }

    public User getUserFromRequest (HttpServletRequest request){
        String currentUserEmail = extractUsername(extractTokenFromRequest(request));
        return userRepository.findByEmail(currentUserEmail).orElseThrow(()->new NoSuchElementException("Couldn't find user in a DB!"));
    }

    public UserInfoDTO getUserRoleAndEmail(HttpServletRequest request){
        String role = extractRole(extractTokenFromRequest(request));
        String email = extractUsername(extractTokenFromRequest(request));
        return new UserInfoDTO(role, email);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(UserDetails userDetails){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", userDetails.getAuthorities().toString());
        return generateToken(extraClaims, userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails)
    {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token){
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        String expDateStr = extractPayloadField(token, "\"exp\":", "}");
        long expDateLong = Long.parseLong(expDateStr);
        return new Date(expDateLong * 1000);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractTokenFromRequest(HttpServletRequest request){
        String authHeader = request.getHeader("cookie") + ";";
        int tokenIndex = authHeader.indexOf("jwtToken=") + 9;
        return authHeader.substring(tokenIndex, authHeader.indexOf(";",tokenIndex));
    }
}
