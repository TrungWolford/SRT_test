package SRT.test.service;

import SRT.test.dto.request.Auth.LoginRequest;
import SRT.test.dto.request.Auth.LogoutRequest;
import SRT.test.dto.request.Auth.RegisterRequest;
import SRT.test.dto.response.Auth.LoginResponse;
import SRT.test.dto.response.Auth.RegisterResponse;

public interface IAuthService {
    // Authentication
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(String refreshToken);
    RegisterResponse register(RegisterRequest request);

    void logout(LogoutRequest request);
}
