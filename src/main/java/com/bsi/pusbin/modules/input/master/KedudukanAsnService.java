package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.KedudukanAsnDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KedudukanAsnService {
    private final KedudukanAsnRepository repository;

    @Transactional(readOnly = true)
    public List<KedudukanAsnDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public KedudukanAsnDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public KedudukanAsnDto create(KedudukanAsnDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdKedudukan(id);
        return dto;
    }

    @Transactional
    public KedudukanAsnDto update(Integer id, KedudukanAsnDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdKedudukan(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
