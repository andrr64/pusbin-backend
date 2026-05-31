package com.bsi.pusbin.modules.grafik;

import com.bsi.pusbin.config.GlobalExceptionHandler;
import com.bsi.pusbin.modules.grafik.schema.GrafikResponse;
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
class GrafikControllerTest {

    @Mock GrafikService grafikService;
    @InjectMocks GrafikController grafikController;

    MockMvc mockMvc;

    private static final List<GrafikResponse> DATA =
            List.of(new GrafikResponse("PNS", 1500L), new GrafikResponse("PPPK", 300L));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(grafikController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getByJenisAsn_returns200WithList() throws Exception {
        when(grafikService.getByJenisAsn()).thenReturn(DATA);

        mockMvc.perform(get("/api/v1/grafik/jenis-asn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].label").value("PNS"))
                .andExpect(jsonPath("$.data[0].jumlah").value(1500));
    }

    @Test
    void getByJenisAsn_emptyDb_returns200EmptyList() throws Exception {
        when(grafikService.getByJenisAsn()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/grafik/jenis-asn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test void getByKedudukan_returns200() throws Exception {
        when(grafikService.getByKedudukan()).thenReturn(DATA);
        mockMvc.perform(get("/api/v1/grafik/kedudukan")).andExpect(status().isOk());
    }

    @Test void getByJenisKelamin_returns200() throws Exception {
        when(grafikService.getByJenisKelamin()).thenReturn(DATA);
        mockMvc.perform(get("/api/v1/grafik/jenis-kelamin")).andExpect(status().isOk());
    }

    @Test void getByPendidikan_returns200() throws Exception {
        when(grafikService.getByPendidikan()).thenReturn(DATA);
        mockMvc.perform(get("/api/v1/grafik/pendidikan")).andExpect(status().isOk());
    }

    @Test void getByGolongan_returns200() throws Exception {
        when(grafikService.getByGolongan()).thenReturn(DATA);
        mockMvc.perform(get("/api/v1/grafik/golongan")).andExpect(status().isOk());
    }

    @Test void getByInstansi_returns200() throws Exception {
        when(grafikService.getByInstansi()).thenReturn(DATA);
        mockMvc.perform(get("/api/v1/grafik/instansi")).andExpect(status().isOk());
    }

    @Test void getByPokja_returns200() throws Exception {
        when(grafikService.getByPokja()).thenReturn(DATA);
        mockMvc.perform(get("/api/v1/grafik/pokja")).andExpect(status().isOk());
    }

    @Test void getByDiklat_returns200() throws Exception {
        when(grafikService.getByDiklat()).thenReturn(DATA);
        mockMvc.perform(get("/api/v1/grafik/diklat")).andExpect(status().isOk());
    }
}
