package com.bsi.pusbin.modules.iam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.bsi.pusbin.config.GlobalExceptionHandler;
import com.bsi.pusbin.modules.iam.schema.LoginRequest;
import com.bsi.pusbin.modules.iam.schema.RegisterRequest;
import com.bsi.pusbin.shared.exception.db.DuplicateResourceException;
import com.bsi.pusbin.shared.exception.service.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IamControllerTest {

    @Mock IamService iamService;
    @InjectMocks IamController iamController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    @SuppressWarnings("removal")
    void setUp() {
        objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(iamController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // --- register ---

    @Test
    void register_validBody_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/iam/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("12345", "pass"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(iamService).register(any(RegisterRequest.class), anyString());
    }

    @Test
    void register_missingNip_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/iam/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_missingPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/iam/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nip\":\"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_duplicateNip_returns409() throws Exception {
        doThrow(new DuplicateResourceException("NIP sudah terdaftar"))
                .when(iamService).register(any(), anyString());

        mockMvc.perform(post("/api/v1/iam/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("12345", "pass"))))
                .andExpect(status().isConflict());
    }

    // --- login ---

    @Test
    void login_validCredentials_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/iam/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("12345", "pass"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(iamService).login(any(LoginRequest.class), anyString(), any());
    }

    @Test
    void login_missingPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/iam/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nip\":\"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        doThrow(new UnauthorizedException("NIP atau password salah"))
                .when(iamService).login(any(), anyString(), any());

        mockMvc.perform(post("/api/v1/iam/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("12345", "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    // --- refresh ---

    @Test
    void refresh_withCookie_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/iam/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "mytoken")))
                .andExpect(status().isOk());

        verify(iamService).refresh(eq("mytoken"), any());
    }

    @Test
    void refresh_invalidToken_returns401() throws Exception {
        doThrow(new UnauthorizedException("Refresh token tidak valid atau sudah kadaluarsa"))
                .when(iamService).refresh(any(), any());

        mockMvc.perform(post("/api/v1/iam/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "bad")))
                .andExpect(status().isUnauthorized());
    }

    // --- logout ---

    @Test
    void logout_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/iam/logout"))
                .andExpect(status().isOk());

        verify(iamService).logout(any());
    }
}
