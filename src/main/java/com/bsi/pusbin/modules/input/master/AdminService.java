package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.AdminDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository repository;

    @Transactional(readOnly = true)
    public List<AdminDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public AdminDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public AdminDto create(AdminDto dto) {
        Integer id = repository.insert(dto);
        dto.setId(id);
        return dto;
    }

    @Transactional
    public AdminDto update(Integer id, AdminDto dto) {
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.setId(id);
        return dto;
    }

    @Transactional
    public void delete(Integer id) {
        findById(id); // ensure exists
        repository.delete(id);
    }
}
