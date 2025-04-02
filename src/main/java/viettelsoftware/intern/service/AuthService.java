package viettelsoftware.intern.service;

import jakarta.transaction.Transactional;
import viettelsoftware.intern.dto.request.ForgotPasswordRequest;
import viettelsoftware.intern.dto.request.LoginRequest;
import viettelsoftware.intern.dto.request.LogoutRequest;
import viettelsoftware.intern.dto.request.RefreshTokenRequest;
import viettelsoftware.intern.dto.response.AuthenticationResponse;

public interface AuthService {
    AuthenticationResponse authenticate(LoginRequest request);

    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    void forgotPassword(ForgotPasswordRequest request);

    @Transactional
    void resetPassword(String key);

    void logout(LogoutRequest token);
}
