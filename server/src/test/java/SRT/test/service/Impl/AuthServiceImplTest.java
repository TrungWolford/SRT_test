package SRT.test.service.Impl;

import SRT.test.dto.request.Auth.LoginRequest;
import SRT.test.dto.request.Auth.RegisterRequest;
import SRT.test.dto.response.Auth.LoginResponse;
import SRT.test.dto.response.Auth.RegisterResponse;
import SRT.test.entity.User;
import SRT.test.exception.AppException;
import SRT.test.exception.ErrorEnum;
import SRT.test.repo.IUserRepository;
import SRT.test.security.CustomUserDetails;
import SRT.test.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtil;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setAccountName("testuser");
        registerRequest.setFullName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");

        loginRequest = new LoginRequest();
        loginRequest.setAccountName("testuser");
        loginRequest.setPassword("Password123!");

        user = new User();
        user.setUserName("uuid-1234");
        user.setAccountName("testuser");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed_password");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByAccountName(registerRequest.getAccountName())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        RegisterResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getAccountName());
        assertEquals("Test User", response.getFullName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenAccountNameExists() {
        when(userRepository.existsByAccountName(registerRequest.getAccountName())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> authService.register(registerRequest));
        assertEquals(ErrorEnum.ACCOUNT_NAME_ALREADY_EXISTS, exception.getErrorEnum());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenEmailExists() {
        when(userRepository.existsByAccountName(registerRequest.getAccountName())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> authService.register(registerRequest));
        assertEquals(ErrorEnum.EMAIL_ALREADY_EXISTS, exception.getErrorEnum());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(userDetails)).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("refresh_token");
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(3600000L);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals("testuser", response.getUser().getAccountName());
    }

    @Test
    void login_ThrowsException_WhenBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AppException exception = assertThrows(AppException.class, () -> authService.login(loginRequest));
        assertEquals(ErrorEnum.INVALID_CREDENTIALS, exception.getErrorEnum());
    }
}
