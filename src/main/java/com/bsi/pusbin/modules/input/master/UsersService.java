package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.UsersDto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository repository;

    @Transactional(readOnly = true)
    public List<UsersDto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public UsersDto findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }

    @Transactional
    public UsersDto create(UsersDto dto) {
        Integer id = repository.insert(dto);
        dto.setId(id);
        return dto;
    }

    @Transactional
    public UsersDto update(Integer id, UsersDto dto) {
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
