package com.bsi.pusbin.modules.filter;

import com.bsi.pusbin.config.GlobalExceptionHandler;
import com.bsi.pusbin.modules.filter.schema.FilterResponse;
import com.bsi.pusbin.shared.exception.db.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FilterControllerTest {

    @Mock FilterService filterService;
    @InjectMocks FilterController filterController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(filterController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getFilter_validTabel_returns200WithList() throws Exception {
        when(filterService.getFilter("jenis_asn"))
                .thenReturn(List.of(new FilterResponse(1, "PNS"), new FilterResponse(2, "PPPK")));

        mockMvc.perform(get("/api/v1/filters/jenis_asn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nama").value("PNS"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getFilter_invalidTabel_returns404() throws Exception {
        when(filterService.getFilter("unknown"))
                .thenThrow(new ResourceNotFoundException("Tabel 'unknown' tidak tersedia"));

        mockMvc.perform(get("/api/v1/filters/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFilter_emptyTable_returns200WithEmptyList() throws Exception {
        when(filterService.getFilter("jenis_asn")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/filters/jenis_asn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
