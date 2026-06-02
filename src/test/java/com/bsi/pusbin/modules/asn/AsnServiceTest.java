/**
package com.bsi.pusbin.modules.asn;

import com.bsi.pusbin.modules.asn.schema.AsnRequest;
import com.bsi.pusbin.modules.asn.schema.AsnResponse;
import com.bsi.pusbin.shared.exception.db.DuplicateResourceException;
import com.bsi.pusbin.shared.exception.db.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsnServiceTest {

    @Mock AsnRepository asnRepository;
    @InjectMocks AsnService asnService;

    private static final AsnResponse SAMPLE = new AsnResponse(
            1001L, 1, 1, 1, 1, 1, 1, 1, 1, null, null, null, null);

    private static AsnRequest request(Long id) {
        return new AsnRequest(id, 1, 1, 1, 1, 1, 1, 1, 1, null, null, null, null);
    }

    // --- getById ---

    @Test
    void getById_found_returnsResponse() {
        when(asnRepository.findById(1001L)).thenReturn(Optional.of(SAMPLE));

        AsnResponse result = asnService.getById(1001L);

        assertThat(result.idAsn()).isEqualTo(1001L);
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(asnRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> asnService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- create ---

    @Test
    void create_newId_callsInsert() {
        when(asnRepository.existsById(1001L)).thenReturn(false);

        asnService.create(request(1001L));

        verify(asnRepository).insert(any());
    }

    @Test
    void create_duplicateId_throwsDuplicateResourceException() {
        when(asnRepository.existsById(1001L)).thenReturn(true);

        assertThatThrownBy(() -> asnService.create(request(1001L)))
                .isInstanceOf(DuplicateResourceException.class);

        verify(asnRepository, never()).insert(any());
    }

    // --- update ---

    @Test
    void update_existingId_callsUpdate() {
        when(asnRepository.existsById(1001L)).thenReturn(true);

        asnService.update(1001L, request(9999L)); // body id should be overridden

        verify(asnRepository).update(argThat(r -> r.idAsn().equals(1001L))); // path id wins
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(asnRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> asnService.update(999L, request(999L)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(asnRepository, never()).update(any());
    }

    // --- delete ---

    @Test
    void delete_existingId_callsDelete() {
        when(asnRepository.existsById(1001L)).thenReturn(true);

        asnService.delete(1001L);

        verify(asnRepository).delete(1001L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(asnRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> asnService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(asnRepository, never()).delete(any());
    }
}
 */