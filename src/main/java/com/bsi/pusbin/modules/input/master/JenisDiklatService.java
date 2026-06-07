package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisDiklatDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JenisDiklatService {
    private final JenisDiklatRepository repository;

    @Transactional(readOnly = true)
    public List<JenisDiklatDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public JenisDiklatDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public JenisDiklatDto create(JenisDiklatDto dto) {
        Integer id = repository.insert(dto);
        dto.setIdJenisDiklat(id);
        return dto;
    }

    @Transactional
    public JenisDiklatDto update(Integer id, JenisDiklatDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setIdJenisDiklat(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
