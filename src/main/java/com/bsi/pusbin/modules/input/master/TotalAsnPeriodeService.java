package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.TotalAsnPeriodeDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TotalAsnPeriodeService {
    private final TotalAsnPeriodeRepository repository;

    @Transactional(readOnly = true)
    public List<TotalAsnPeriodeDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public TotalAsnPeriodeDto findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public TotalAsnPeriodeDto create(TotalAsnPeriodeDto dto) {
        Long id = repository.insert(dto);
        dto.setId(id);
        return dto;
    }

    @Transactional
    public TotalAsnPeriodeDto update(Long id, TotalAsnPeriodeDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setId(id);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
