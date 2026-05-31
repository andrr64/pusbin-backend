package com.bsi.pusbin.modules.export;

import com.bsi.pusbin.config.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExportControllerTest {

    @Mock ExportService exportService;
    @InjectMocks ExportController exportController;

    MockMvc mockMvc;

    private static final byte[] FAKE_XLSX = new byte[]{0x50, 0x4B, 0x03, 0x04};

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(exportController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void export_noFilters_returns200WithXlsxContentType() throws Exception {
        when(exportService.exportToXlsx(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(FAKE_XLSX);

        mockMvc.perform(get("/api/v1/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition",
                        matchesPattern("attachment; filename=\"data-asn-\\d{4}-\\d{2}-\\d{2}\\.xlsx\"")));
    }

    @Test
    void export_withFilters_passesFiltersToService() throws Exception {
        when(exportService.exportToXlsx(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(FAKE_XLSX);

        mockMvc.perform(get("/api/v1/export")
                        .param("jenisAsnId", "1")
                        .param("pokjaId", "2"))
                .andExpect(status().isOk());

        verify(exportService).exportToXlsx(eq(2), any(), eq(1), any(), any(), any(), any(), any());
    }

    @Test
    void export_emptyResult_returns200() throws Exception {
        when(exportService.exportToXlsx(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new byte[0]);

        mockMvc.perform(get("/api/v1/export"))
                .andExpect(status().isOk());
    }
}
