package com.bsi.pusbin.modules.iam;

import com.bsi.pusbin.modules.iam.schema.LoginRequest;
import com.bsi.pusbin.modules.iam.schema.RegisterRequest;
import com.bsi.pusbin.shared.exception.AppException;
import com.bsi.pusbin.shared.exception.db.DuplicateResourceException;
import com.bsi.pusbin.shared.exception.service.UnauthorizedException;
import com.bsi.pusbin.shared.security.JwtProperties;
import com.bsi.pusbin.shared.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamServiceTest {

    @Mock IamRepository iamRepository;
    @Mock JwtProvider jwtProvider;
    @Mock JwtProperties jwtProperties;
    @Mock Argon2PasswordEncoder passwordEncoder;
    @Mock RateLimiter rateLimiter;

    @InjectMocks IamService iamService;

    // --- register ---

    @Test
    void register_happyPath_savesHashedPassword() {
        when(iamRepository.existsByNip("12345")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("hashed");

        iamService.register(new RegisterRequest("12345", "pass"), "127.0.0.1");

        verify(iamRepository).saveAdmin("12345", "hashed");
    }

    @Test
    void register_duplicateNip_throwsDuplicateResourceException() {
        when(iamRepository.existsByNip("12345")).thenReturn(true);

        assertThatThrownBy(() -> iamService.register(new RegisterRequest("12345", "pass"), "127.0.0.1"))
                .isInstanceOf(DuplicateResourceException.class);

        verify(iamRepository, never()).saveAdmin(any(), any());
    }

    @Test
    void register_rateLimitExceeded_throwsBeforeDbAccess() {
        doThrow(new AppException("Too many", org.springframework.http.HttpStatus.TOO_MANY_REQUESTS))
                .when(rateLimiter).check("register", "127.0.0.1");

        assertThatThrownBy(() -> iamService.register(new RegisterRequest("12345", "pass"), "127.0.0.1"))
                .isInstanceOf(AppException.class);

        verify(iamRepository, never()).existsByNip(any());
    }

    // --- login ---

    @Test
    void login_happyPath_setsCookies() {
        IamRepository.AdminRecord admin = new IamRepository.AdminRecord(1, "12345", "hashed");
        when(iamRepository.findByNip("12345")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
        when(jwtProvider.generateAccessToken("12345")).thenReturn("access");
        when(jwtProvider.generateRefreshToken()).thenReturn("refresh");
        when(jwtProvider.hashRefreshToken("refresh")).thenReturn("hash");
        when(jwtProperties.getRefreshTokenExpiryMs()).thenReturn(259200000L);
        when(jwtProperties.getAccessTokenExpiryMs()).thenReturn(900000L);

        MockHttpServletResponse response = new MockHttpServletResponse();
        iamService.login(new LoginRequest("12345", "pass"), "127.0.0.1", response);

        assertThat(response.getCookie("access_token")).isNotNull();
        assertThat(response.getCookie("refresh_token")).isNotNull();
        assertThat(response.getCookie("access_token").isHttpOnly()).isTrue();
        assertThat(response.getCookie("access_token").getSecure()).isTrue();
        verify(iamRepository).saveRefreshToken(eq(1), eq("hash"), any(Timestamp.class));
    }

    @Test
    void login_nipNotFound_throwsUnauthorized() {
        when(iamRepository.findByNip("12345")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> iamService.login(new LoginRequest("12345", "pass"), "127.0.0.1", new MockHttpServletResponse()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        IamRepository.AdminRecord admin = new IamRepository.AdminRecord(1, "12345", "hashed");
        when(iamRepository.findByNip("12345")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> iamService.login(new LoginRequest("12345", "wrong"), "127.0.0.1", new MockHttpServletResponse()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_rateLimitExceeded_throwsBeforeDbAccess() {
        doThrow(new AppException("Too many", org.springframework.http.HttpStatus.TOO_MANY_REQUESTS))
                .when(rateLimiter).check("login", "127.0.0.1");

        assertThatThrownBy(() -> iamService.login(new LoginRequest("12345", "pass"), "127.0.0.1", new MockHttpServletResponse()))
                .isInstanceOf(AppException.class);

        verify(iamRepository, never()).findByNip(any());
    }

    // --- refresh ---

    @Test
    void refresh_happyPath_rotatesTokenAndSetsCookies() {
        when(jwtProvider.hashRefreshToken("oldRaw")).thenReturn("oldHash");
        when(iamRepository.findNipByRefreshToken("oldHash")).thenReturn(Optional.of("12345"));
        when(jwtProvider.generateAccessToken("12345")).thenReturn("newAccess");
        when(jwtProvider.generateRefreshToken()).thenReturn("newRaw");
        when(jwtProvider.hashRefreshToken("newRaw")).thenReturn("newHash");
        when(jwtProperties.getRefreshTokenExpiryMs()).thenReturn(259200000L);
        when(jwtProperties.getAccessTokenExpiryMs()).thenReturn(900000L);
        IamRepository.AdminRecord admin = new IamRepository.AdminRecord(1, "12345", "hashed");
        when(iamRepository.findByNip("12345")).thenReturn(Optional.of(admin));

        MockHttpServletResponse response = new MockHttpServletResponse();
        iamService.refresh("oldRaw", response);

        verify(iamRepository).deleteRefreshToken("oldHash");
        verify(iamRepository).saveRefreshToken(eq(1), eq("newHash"), any(Timestamp.class));
        assertThat(response.getCookie("access_token").getValue()).isEqualTo("newAccess");
        assertThat(response.getCookie("refresh_token").getValue()).isEqualTo("newRaw");
    }

    @Test
    void refresh_nullToken_throwsUnauthorized() {
        assertThatThrownBy(() -> iamService.refresh(null, new MockHttpServletResponse()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void refresh_invalidHash_throwsUnauthorized() {
        when(jwtProvider.hashRefreshToken("bad")).thenReturn("badHash");
        when(iamRepository.findNipByRefreshToken("badHash")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> iamService.refresh("bad", new MockHttpServletResponse()))
                .isInstanceOf(UnauthorizedException.class);
    }

    // --- logout ---

    @Test
    void logout_clearsBothCookies() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        iamService.logout(response);

        Cookie access = response.getCookie("access_token");
        Cookie refresh = response.getCookie("refresh_token");
        assertThat(access).isNotNull();
        assertThat(refresh).isNotNull();
        assertThat(access.getMaxAge()).isZero();
        assertThat(refresh.getMaxAge()).isZero();
    }
}
