package viettelsoftware.intern.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import viettelsoftware.intern.config.Jwt.JwtUtil;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.request.*;
import viettelsoftware.intern.dto.response.AuthenticationResponse;
import viettelsoftware.intern.entity.InvalidatedToken;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.repository.InvalidatedTokenRepository;
import viettelsoftware.intern.repository.UserRepository;
import viettelsoftware.intern.service.AuthService;
import viettelsoftware.intern.util.CommonUtil;
import viettelsoftware.intern.util.EmailUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    JwtUtil jwtUtil;
    PasswordEncoder passwordEncoder;
    RedisTemplate<String, String> redisTemplate;
    EmailUtil emailUtil;
    UserRepository userRepository;
    final String RESET_PW_PREFIX = "reset_pw_";
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.secret}")
    protected String SECRET_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public String generateToken(UserEntity userEntity) {
        return Jwts.builder()
                .subject(userEntity.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + VALID_DURATION))
                .signWith(jwtUtil.getSignInKey())
                .claim("scope", buildScope(userEntity))
                .compact();
    }

    private String buildScope(UserEntity userEntity) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(userEntity.getRoles())) {
            userEntity.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());

                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission ->
                            stringJoiner.add(permission.getName())
                    );
                }
            });
        }
        return stringJoiner.toString();
    }

    @Override
    public AuthenticationResponse authenticate(LoginRequest request) {
        UserEntity userEntity = userRepository.findByUsernameIgnoreCase(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var token = generateToken(userEntity);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public boolean isTokenValid(String token) {
        try {
            verifyToken(token, false);
            return !jwtUtil.isTokenExpired(token) && !invalidatedTokenRepository.existsById(jwtUtil.getClaims(token).getId());
        } catch (AppException e) {
            return false;
        }
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getToken();

        verifyToken(refreshToken, true);

        String username = jwtUtil.extractUsername(refreshToken);

        UserEntity userEntity = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String newToken = generateToken(userEntity); // Tạo JWT mới

        return AuthenticationResponse.builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        String token = request.getToken();

        try {
            verifyToken(token, false);

            if (invalidatedTokenRepository.existsById(jwtUtil.getClaims(token).getId())) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }

            invalidatedTokenRepository.save(InvalidatedToken.builder()
                    .id(jwtUtil.getClaims(token).getId())
                    .expiryTime(jwtUtil.getClaims(token).getExpiration())
                    .build());
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    public void verifyToken(String token, boolean isRefreshToken) {
        if (!isTokenValid(token)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        if (isRefreshToken) {
            Claims claims = jwtUtil.getClaims(token);

            Date expiration = claims.getExpiration();
            long refreshExpirationTime = System.currentTimeMillis() + REFRESHABLE_DURATION;

            if (expiration.before(new Date(refreshExpirationTime))) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
        }
    }



    @Transactional
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        if (!CommonUtil.isValidEmail(request.getEmail()))
            throw new AppException(ErrorCode.INVALID_EMAIL);

        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String uuid = UUID.randomUUID().toString();
        String email = user.getEmail().trim();

        // remove old key
        String keyGetUUID = RESET_PW_PREFIX + email;
        String oldUUID = redisTemplate.opsForValue().get(keyGetUUID);
        if (oldUUID != null)
            redisTemplate.delete(RESET_PW_PREFIX + oldUUID);

        redisTemplate.opsForValue().set(keyGetUUID, uuid, 30, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RESET_PW_PREFIX + uuid, email, 30, TimeUnit.MINUTES);

        String url = "http://localhost:8080/identity/auth/reset-password?key=" + uuid;

        Map<String, Object> param = Map.of("username", user.getUsername(), "url", url);

        EmailObjectRequest emailRequest = EmailObjectRequest.builder()
                .emailTo(new String[]{user.getEmail()})
                .subject("admin")
                .template("email-reset-password")
                .params(param)
                .build();

        log.info("Email Request: {}",emailRequest);
        emailUtil.sendEmail(emailRequest);
    }

    @Transactional
    @Override
    public void resetPassword(String key) {
        String keyGetEmail = RESET_PW_PREFIX + key;
        String email = redisTemplate.opsForValue().get(keyGetEmail);
        if (email == null) {
            throw new AppException(ErrorCode.INVALID_EMAIL);
        }
        String keyGetUUID = RESET_PW_PREFIX + email;
        String uuid = redisTemplate.opsForValue().get(keyGetUUID);
        if (uuid != null) {
            redisTemplate.delete(keyGetEmail);
            redisTemplate.delete(keyGetUUID);
        }

        UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        String newPassword = RandomStringUtils.randomAlphanumeric(10);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Map<String, Object> params = Map.of("username", user.getUsername(),
                "newPassword", newPassword);

        EmailObjectRequest emailRequest = EmailObjectRequest.builder()
                .emailTo(new String[]{user.getEmail()})
                .subject("email-new-password")
                .template("email-new-password")
                .params(params)
                .build();
        emailUtil.sendEmail(emailRequest);
    }
}
