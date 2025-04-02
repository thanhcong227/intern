package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.request.ForgotPasswordRequest;
import viettelsoftware.intern.dto.request.LoginRequest;
import viettelsoftware.intern.dto.request.LogoutRequest;
import viettelsoftware.intern.dto.request.RefreshTokenRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.AuthenticationResponse;
import viettelsoftware.intern.service.AuthService;
import viettelsoftware.intern.service.impl.AuthServiceImpl;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthenticationManager authenticationManager;
    AuthServiceImpl authService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            return ApiResponse.<AuthenticationResponse>builder().result(authService.authenticate(request)).build();

        } catch (AuthenticationException e) {
            log.info("Error: {}", e.getMessage());
            return ApiResponse.<AuthenticationResponse>builder().code(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT.getCode()).message(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT.getMessages()).build();
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Object> logout(@RequestBody LogoutRequest token) {
        authService.logout(token);
        return ApiResponse.builder().build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshTokenRequest token) {
        return ApiResponse.<AuthenticationResponse>builder().result(authService.refreshToken(token)).build();
    }

    @PostMapping("/forgotPassword")
    public ApiResponse<Object> forgotPassword(@RequestBody ForgotPasswordRequest email) {
        authService.forgotPassword(email);
        return ApiResponse.builder().build();
    }

    @GetMapping("/reset-password")
    public ApiResponse<Object> resetPassword(@RequestParam String key) {
        authService.resetPassword(key);
        return ApiResponse.builder().build();
    }
}
