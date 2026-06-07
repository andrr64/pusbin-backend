package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisAsnDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenisAsnService {
    private final JenisAsnRepository repository;

    @Transactional(readOnly = true)
    public List<JenisAsnDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public JenisAsnDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public JenisAsnDto create(JenisAsnDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdJenisAsn(id);
        return dto;
    }

    @Transactional
    public JenisAsnDto update(Integer id, JenisAsnDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdJenisAsn(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
