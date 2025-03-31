package viettelsoftware.intern.service;

import viettelsoftware.intern.dto.request.LoginRequest;

public interface AuthService {
    void login(LoginRequest request);
}
