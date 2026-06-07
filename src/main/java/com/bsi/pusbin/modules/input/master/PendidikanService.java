package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.PendidikanDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PendidikanService {
    private final PendidikanRepository repository;

    @Transactional(readOnly = true)
    public List<PendidikanDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PendidikanDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public PendidikanDto create(PendidikanDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdPendidikan(id);
        return dto;
    }

    @Transactional
    public PendidikanDto update(Integer id, PendidikanDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdPendidikan(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
