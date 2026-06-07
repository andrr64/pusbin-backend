package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.WilayahBknDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WilayahBknService {
    private final WilayahBknRepository repository;

    @Transactional(readOnly = true)
    public List<WilayahBknDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public WilayahBknDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public WilayahBknDto create(WilayahBknDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdWilker(id);
        return dto;
    }

    @Transactional
    public WilayahBknDto update(Integer id, WilayahBknDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdWilker(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
