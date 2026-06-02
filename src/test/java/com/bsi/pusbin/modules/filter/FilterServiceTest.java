/**
package com.bsi.pusbin.modules.filter;

import com.bsi.pusbin.modules.filter.schema.FilterResponse;
import com.bsi.pusbin.shared.exception.db.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilterServiceTest {

    @Mock FilterRepository filterRepository;
    @InjectMocks FilterService filterService;

    @Test
    void getFilter_validTabel_returnsList() {
        String sql = "SELECT id_jenis_asn AS id, nama_jenis AS nama FROM jenis_asn";
        List<FilterResponse> expected = List.of(new FilterResponse(1, "PNS"), new FilterResponse(2, "PPPK"));
        when(filterRepository.resolveQuery("jenis_asn")).thenReturn(Optional.of(sql));
        when(filterRepository.findByQuery(sql)).thenReturn(expected);

        List<FilterResponse> result = filterService.getFilter("jenis_asn");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getFilter_invalidTabel_throwsResourceNotFoundException() {
        when(filterRepository.resolveQuery("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filterService.getFilter("unknown"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(filterRepository, never()).findByQuery(any());
    }

    @Test
    void getFilter_emptyTable_returnsEmptyList() {
        String sql = "SELECT ...";
        when(filterRepository.resolveQuery("jenis_asn")).thenReturn(Optional.of(sql));
        when(filterRepository.findByQuery(sql)).thenReturn(List.of());

        List<FilterResponse> result = filterService.getFilter("jenis_asn");

        assertThat(result).isEmpty();
    }
}
 */