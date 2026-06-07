package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.InstansiDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstansiService {
    private final InstansiRepository repository;

    @Transactional(readOnly = true)
    public List<InstansiDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public InstansiDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public InstansiDto create(InstansiDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdInstansi(id);
        return dto;
    }

    @Transactional
    public InstansiDto update(Integer id, InstansiDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdInstansi(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
