/**
package com.bsi.pusbin.modules.grafik;

import com.bsi.pusbin.modules.grafik.schema.GrafikResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrafikServiceTest {

    @Mock GrafikRepository grafikRepository;
    @InjectMocks GrafikService grafikService;

    private static final List<GrafikResponse> DATA = List.of(
            new GrafikResponse("PNS", 1500L), new GrafikResponse("PPPK", 300L));

    @Test void getByJenisAsn_returnsRepositoryResult() {
        when(grafikRepository.countByJenisAsn()).thenReturn(DATA);
        assertThat(grafikService.getByJenisAsn()).isEqualTo(DATA);
    }

    @Test void getByJenisAsn_emptyDb_returnsEmptyList() {
        when(grafikRepository.countByJenisAsn()).thenReturn(List.of());
        assertThat(grafikService.getByJenisAsn()).isEmpty();
    }

    @Test void getByKedudukan_returnsRepositoryResult() {
        when(grafikRepository.countByKedudukan()).thenReturn(DATA);
        assertThat(grafikService.getByKedudukan()).isEqualTo(DATA);
    }

    @Test void getByJenisKelamin_returnsRepositoryResult() {
        when(grafikRepository.countByJenisKelamin()).thenReturn(DATA);
        assertThat(grafikService.getByJenisKelamin()).isEqualTo(DATA);
    }

    @Test void getByPendidikan_returnsRepositoryResult() {
        when(grafikRepository.countByPendidikan()).thenReturn(DATA);
        assertThat(grafikService.getByPendidikan()).isEqualTo(DATA);
    }

    @Test void getByGolongan_returnsRepositoryResult() {
        when(grafikRepository.countByGolongan()).thenReturn(DATA);
        assertThat(grafikService.getByGolongan()).isEqualTo(DATA);
    }

    @Test void getByInstansi_returnsRepositoryResult() {
        when(grafikRepository.countByInstansi()).thenReturn(DATA);
        assertThat(grafikService.getByInstansi()).isEqualTo(DATA);
    }

    @Test void getByPokja_returnsRepositoryResult() {
        when(grafikRepository.countByPokja()).thenReturn(DATA);
        assertThat(grafikService.getByPokja()).isEqualTo(DATA);
    }

    @Test void getByDiklat_returnsRepositoryResult() {
        when(grafikRepository.countByDiklat()).thenReturn(DATA);
        assertThat(grafikService.getByDiklat()).isEqualTo(DATA);
    }
}
*/