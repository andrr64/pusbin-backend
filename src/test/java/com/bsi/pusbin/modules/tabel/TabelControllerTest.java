/**
package com.bsi.pusbin.modules.tabel;

import com.bsi.pusbin.config.GlobalExceptionHandler;
import com.bsi.pusbin.modules.tabel.schema.TabelPageResponse;
import com.bsi.pusbin.modules.tabel.schema.TabelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TabelControllerTest {

    @Mock TabelService tabelService;
    @InjectMocks TabelController tabelController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(tabelController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private TabelPageResponse emptyPage() {
        return new TabelPageResponse(List.of(), 0, 20, 0L, 0);
    }

    private TabelPageResponse pageWithData() {
        TabelResponse row = new TabelResponse(1L, "PNS", null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        return new TabelPageResponse(List.of(row), 0, 20, 1L, 1);
    }

    @Test
    void getTabel_noFilters_returns200WithPagedResult() throws Exception {
        when(tabelService.getTabel(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(pageWithData());

        mockMvc.perform(get("/api/v1/tabel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20));
    }

    @Test
    void getTabel_withFilters_passesFiltersToService() throws Exception {
        when(tabelService.getTabel(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(emptyPage());

        mockMvc.perform(get("/api/v1/tabel")
                        .param("jenisAsnId", "1")
                        .param("pokjaId", "2")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(tabelService).getTabel(eq(2), any(), eq(1), any(), any(), any(), any(), any(), eq(1), eq(10));
    }

    @Test
    void getTabel_emptyResult_returns200WithEmptyContent() throws Exception {
        when(tabelService.getTabel(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(emptyPage());

        mockMvc.perform(get("/api/v1/tabel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void getTabel_invalidPageType_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/tabel").param("page", "abc"))
                .andExpect(status().isBadRequest());
    }
}
 */