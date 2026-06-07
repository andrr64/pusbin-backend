package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.NomenklaturDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NomenklaturService {
    private final NomenklaturRepository repository;

    @Transactional(readOnly = true)
    public List<NomenklaturDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public NomenklaturDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public NomenklaturDto create(NomenklaturDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdNomenklatur(id);
        return dto;
    }

    @Transactional
    public NomenklaturDto update(Integer id, NomenklaturDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdNomenklatur(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
