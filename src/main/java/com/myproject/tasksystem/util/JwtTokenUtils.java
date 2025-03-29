package com.myproject.tasksystem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {
     @Value("${jwt.secret}")
     private String secretKey;

     @Value("${jwt.lifetime}")
     private Duration jwtLifetime;

     public String getUsername(String token) {
         return extractAllClaims(token).getSubject();
     }

     public List<String> getRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
     }

    //Извлечение данных из токена
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) { //claimsResolvers функция извлечения данных
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    // Извлечение всех данных из токена
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    //Когда приходит логин и пароль, то формирует токен и возвращает его пользователю
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> data = new HashMap<>(); // данные для PayLoad
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // конвертировать в String
                .collect(Collectors.toList()); //набор ролей для Токена в формате String
        data.put("roles", rolesList);

        Date issuedDate = new Date(); // время создания токена
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());// время когда истечет время жизни
        return Jwts.builder() //формируем Payload
                .setClaims(data)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey) //SIGNATURE
                .compact();
    }

    //Проверка токена на просроченность
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); //return true, если токен просрочен
    }

    //Извлечение даты истечения токена
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //Получение ключа для подписи токена
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
