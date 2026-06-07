package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.WilayahPokjaDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WilayahPokjaService {
    private final WilayahPokjaRepository repository;

    @Transactional(readOnly = true)
    public List<WilayahPokjaDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public WilayahPokjaDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public WilayahPokjaDto create(WilayahPokjaDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdWilayahPokja(id);
        return dto;
    }

    @Transactional
    public WilayahPokjaDto update(Integer id, WilayahPokjaDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdWilayahPokja(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
