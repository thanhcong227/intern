package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.config.Jwt.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.request.LoginRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.LoginResponse;

@RestController
@RequestMapping("/auth")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            return ApiResponse.<LoginResponse>builder().result(jwtUtil.authenticate(request)).build();

        } catch (AuthenticationException e) {
            log.info("Error: {}", e.getMessage());
            return ApiResponse.<LoginResponse>builder().code(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT.getCode()).message(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT.getMessages()).build();
        }
    }
}
