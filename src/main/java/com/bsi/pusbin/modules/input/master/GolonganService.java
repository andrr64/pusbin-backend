package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.GolonganDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GolonganService {
    private final GolonganRepository repository;

    @Transactional(readOnly = true)
    public List<GolonganDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public GolonganDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public GolonganDto create(GolonganDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdGolongan(id);
        return dto;
    }

    @Transactional
    public GolonganDto update(Integer id, GolonganDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdGolongan(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
