package sobad.code.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sobad.code.dtos.JwtTokenPojo;
import sobad.code.dtos.TokenDtoResponse;
import sobad.code.dtos.UserDtoRequest;
import sobad.code.entities.User;
import sobad.code.jwt.Token;
import sobad.code.mappers.TokenSerializer;
import sobad.code.repositories.TokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final TokenSerializer tokenSerializer;
    private final TokenRepository tokenRepository;

    @Override
    public TokenDtoResponse authenticateUser(UserDtoRequest userDtoRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDtoRequest.getUsername(),
                        userDtoRequest.getPassword()
                )
        );

        UserDetails userDetails = userService.loadUserByUsername(userDtoRequest.getUsername());
        User user = userService.findByUsername(userDetails.getUsername());

        JwtTokenPojo jwtTokenPojo = JwtTokenPojo.builder()
                .tokenId(UUID.randomUUID())
                .userId(user.getId())
                .username(user.getUsername())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plus(2, DAYS))
                .build();

        String serializedToken = tokenSerializer.apply(jwtTokenPojo);

        Token token = Token.builder()
                .accessToken(serializedToken)
                .createdAt(jwtTokenPojo.getCreatedAt())
                .expiredAt(jwtTokenPojo.getExpiredAt())
                .build();

        tokenRepository.save(token);

        return new TokenDtoResponse(serializedToken);
    }
}
