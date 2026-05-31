package com.bsi.pusbin.modules.tabel;

import com.bsi.pusbin.modules.tabel.schema.TabelPageResponse;
import com.bsi.pusbin.modules.tabel.schema.TabelResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TabelServiceTest {

    @Mock TabelRepository tabelRepository;
    @InjectMocks TabelService tabelService;

    private static final TabelResponse ROW = new TabelResponse(
            1L, "PNS", "Pusat", "L", "S1", "Instansi", "Pusat",
            "Jabatan", "Muda", "III/c", "Diklat", "Wilker", "Pokja",
            null, null, null, null);

    // --- getTabel ---

    @Test
    void getTabel_noFilters_returnsPagedResult() {
        when(tabelRepository.count(any())).thenReturn(40L);
        when(tabelRepository.findAll(any(), eq(0), eq(20))).thenReturn(List.of(ROW));

        TabelPageResponse result = tabelService.getTabel(null, null, null, null, null, null, null, null, 0, 20);

        assertThat(result.total()).isEqualTo(40L);
        assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.content()).hasSize(1);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(20);
    }

    @Test
    void getTabel_emptyResult_returnsZeroTotal() {
        when(tabelRepository.count(any())).thenReturn(0L);
        when(tabelRepository.findAll(any(), eq(0), eq(20))).thenReturn(List.of());

        TabelPageResponse result = tabelService.getTabel(null, null, null, null, null, null, null, null, 0, 20);

        assertThat(result.total()).isZero();
        assertThat(result.totalPages()).isZero();
        assertThat(result.content()).isEmpty();
    }

    @Test
    void getTabel_totalPages_roundsUp() {
        when(tabelRepository.count(any())).thenReturn(41L);
        when(tabelRepository.findAll(any(), anyInt(), anyInt())).thenReturn(List.of());

        TabelPageResponse result = tabelService.getTabel(null, null, null, null, null, null, null, null, 0, 20);

        assertThat(result.totalPages()).isEqualTo(3);
    }

    @Test
    void getTabel_withFilters_passesFilterParamsToRepository() {
        when(tabelRepository.count(any())).thenReturn(5L);
        when(tabelRepository.findAll(any(), anyInt(), anyInt())).thenReturn(List.of());

        tabelService.getTabel(1, 2, 3, 4, 5, 6, 7, 8, 0, 10);

        verify(tabelRepository).count(argThat(p ->
                p.pokjaId() == 1 && p.instansiId() == 2 && p.jenisAsnId() == 3));
    }
}
