package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisJfDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenisJfService {
    private final JenisJfRepository repository;

    @Transactional(readOnly = true)
    public List<JenisJfDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public JenisJfDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public JenisJfDto create(JenisJfDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdJenisJf(id);
        return dto;
    }

    @Transactional
    public JenisJfDto update(Integer id, JenisJfDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdJenisJf(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
