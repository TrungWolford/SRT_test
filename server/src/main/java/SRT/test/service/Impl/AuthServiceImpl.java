package SRT.test.service.Impl;

import SRT.test.dto.request.Auth.LoginRequest;
import SRT.test.dto.request.Auth.LogoutRequest;
import SRT.test.dto.request.Auth.RegisterRequest;
import SRT.test.dto.response.Auth.LoginResponse;
import SRT.test.dto.response.Auth.RegisterResponse;
import SRT.test.entity.User;
import SRT.test.exception.AppException;
import SRT.test.exception.ErrorEnum;
import SRT.test.repo.IUserRepository;
import SRT.test.security.CustomUserDetails;
import SRT.test.security.JwtUtils;
import SRT.test.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getAccountName(),
                            request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            assert userDetails != null;
            User user = userDetails.getUser();

            userRepository.save(user);

            // Build user info for response
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                    .userName(user.getUserName())
                    .accountName(user.getAccountName())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .build();

            // Generate refresh token first
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Create login session
            String refreshTokenHash = hashToken(refreshToken);


            // Generate access token with sessionId
            String accessToken = jwtUtil.generateAccessToken(userDetails);

            return LoginResponse.success(
                    accessToken,
                    refreshToken,
                    jwtUtil.getAccessTokenExpiration() / 1000,
                    userInfo);

        } catch (BadCredentialsException e) {
            throw new AppException(ErrorEnum.INVALID_CREDENTIALS);
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        try {
            String username = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new AppException(ErrorEnum.USER_NOT_FOUND));

            CustomUserDetails userDetails = new CustomUserDetails(user);

            if (!jwtUtil.validateToken(refreshToken, userDetails)) {
                throw new AppException(ErrorEnum.REFRESH_TOKEN_INVALID);
            }

            // Generate new tokens (include sessionId in access token)
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                    .userName(user.getUserName())
                    .accountName(user.getAccountName())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .build();

            return LoginResponse.success(
                    newAccessToken,
                    newRefreshToken,
                    jwtUtil.getAccessTokenExpiration() / 1000,
                    userInfo);

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new AppException(ErrorEnum.REFRESH_TOKEN_INVALID);
        }
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // Validate if accountName already exists
        if (userRepository.existsByAccountName(request.getAccountName())) {
            throw new AppException(ErrorEnum.ACCOUNT_NAME_ALREADY_EXISTS);
        }

        // Validate if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorEnum.EMAIL_ALREADY_EXISTS);
        }


        // Create new user
        User user = new User();
        user.setAccountName(request.getAccountName());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedDate(new Date());
        user.setUpdatedDate(new Date());

        User savedUser = userRepository.save(user);

        return RegisterResponse.success(savedUser.getUserName(), savedUser.getAccountName(), savedUser.getFullName());
    }

    @Override
    public void logout(LogoutRequest request) {
        if (request.getRefreshToken() != null) {
            // Logout by refresh token
            String refreshTokenHash = hashToken(request.getRefreshToken());
        } else {
            throw new AppException(ErrorEnum.BAD_REQUEST);
        }
    }


    /**
     * Hash token using SHA-256
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot find SHA-256", e);
        }
    }
}
