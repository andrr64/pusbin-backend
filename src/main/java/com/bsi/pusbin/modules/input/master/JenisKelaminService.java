package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisKelaminDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenisKelaminService {
    private final JenisKelaminRepository repository;

    @Transactional(readOnly = true)
    public List<JenisKelaminDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public JenisKelaminDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public JenisKelaminDto create(JenisKelaminDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdJenisKelamin(id);
        return dto;
    }

    @Transactional
    public JenisKelaminDto update(Integer id, JenisKelaminDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdJenisKelamin(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
