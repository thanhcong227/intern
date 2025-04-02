package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.dto.request.ForgotPasswordRequest;
import viettelsoftware.intern.dto.request.LoginRequest;
import viettelsoftware.intern.dto.request.LogoutRequest;
import viettelsoftware.intern.dto.request.RefreshTokenRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.AuthenticationResponse;
import viettelsoftware.intern.service.impl.AuthServiceImpl;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthenticationManager authenticationManager;
    AuthServiceImpl authService;
    ResponseFactory responseFactory;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<AuthenticationResponse>> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            return responseFactory.success(authService.authenticate(request));
        } catch (AuthenticationException e) {
            log.info("Error: {}", e.getMessage());
            return responseFactory.fail(ResponseStatusCodeEnum.USERNAME_OR_PASSWORD_INCORRECT);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse<Object>> logout(@RequestBody LogoutRequest token) {
        authService.logout(token);
        return responseFactory.successNoData();
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
