package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JabatanDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JabatanService {
    private final JabatanRepository repository;

    @Transactional(readOnly = true)
    public List<JabatanDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public JabatanDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public JabatanDto create(JabatanDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdJabatan(id);
        return dto;
    }

    @Transactional
    public JabatanDto update(Integer id, JabatanDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdJabatan(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
