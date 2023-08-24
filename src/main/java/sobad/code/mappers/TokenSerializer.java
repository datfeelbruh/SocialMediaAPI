package sobad.code.mappers;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.dtos.JwtTokenPojo;
import sobad.code.jwt.JwtUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Component
@RequiredArgsConstructor
public class TokenSerializer implements Function<JwtTokenPojo, String> {
    private final JwtUtil jwtUtil;

    @Override
    public String apply(JwtTokenPojo jwtTokenPojo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", jwtTokenPojo.getAuthorities());
        claims.put("userId", jwtTokenPojo.getUserId());
        claims.put("tokenId", jwtTokenPojo.getTokenId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(jwtTokenPojo.getUsername())
                .setIssuedAt(Date.from(jwtTokenPojo.getCreatedAt()))
                .setExpiration(Date.from(jwtTokenPojo.getExpiredAt()))
                .signWith(jwtUtil.getSignKey(), HS256)
                .compact();
    }
}
