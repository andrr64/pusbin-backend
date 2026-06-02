/***
 * package com.bsi.pusbin.modules.asn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.bsi.pusbin.config.GlobalExceptionHandler;
import com.bsi.pusbin.modules.asn.schema.AsnRequest;
import com.bsi.pusbin.modules.asn.schema.AsnResponse;
import com.bsi.pusbin.shared.exception.db.DuplicateResourceException;
import com.bsi.pusbin.shared.exception.db.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AsnControllerTest {

    @Mock AsnService asnService;
    @InjectMocks AsnController asnController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(asnController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private static final AsnResponse SAMPLE = new AsnResponse(
            1001L, 1, 1, 1, 1, 1, 1, 1, 1, null, null, null, null);

    private String requestJson(Long id) throws Exception {
        return objectMapper.writeValueAsString(
                new AsnRequest(id, 1, 1, 1, 1, 1, 1, 1, 1, null, null, null, null));
    }

    // --- GET ---

    @Test
    void getById_found_returns200() throws Exception {
        when(asnService.getById(1001L)).thenReturn(SAMPLE);

        mockMvc.perform(get("/api/v1/asn/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.idAsn").value(1001));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(asnService.getById(999L)).thenThrow(new ResourceNotFoundException("not found"));

        mockMvc.perform(get("/api/v1/asn/999"))
                .andExpect(status().isNotFound());
    }

    // --- POST ---

    @Test
    void create_validBody_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/asn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(1001L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(asnService).create(any(AsnRequest.class));
    }

    @Test
    void create_missingIdAsn_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/asn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idJenisAsn\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_duplicate_returns409() throws Exception {
        doThrow(new DuplicateResourceException("already exists")).when(asnService).create(any());

        mockMvc.perform(post("/api/v1/asn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(1001L)))
                .andExpect(status().isConflict());
    }

    // --- PUT ---

    @Test
    void update_existingId_returns200() throws Exception {
        mockMvc.perform(put("/api/v1/asn/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(1001L)))
                .andExpect(status().isOk());

        verify(asnService).update(eq(1001L), any(AsnRequest.class));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("not found")).when(asnService).update(eq(999L), any());

        mockMvc.perform(put("/api/v1/asn/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(999L)))
                .andExpect(status().isNotFound());
    }

    // --- DELETE ---

    @Test
    void delete_existingId_returns200() throws Exception {
        mockMvc.perform(delete("/api/v1/asn/1001"))
                .andExpect(status().isOk());

        verify(asnService).delete(1001L);
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("not found")).when(asnService).delete(999L);

        mockMvc.perform(delete("/api/v1/asn/999"))
                .andExpect(status().isNotFound());
    }
}
*/