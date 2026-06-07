package com.bsi.pusbin.modules.input;

import com.bsi.pusbin.modules.input.schema.InputPageResponse;
import com.bsi.pusbin.modules.input.schema.InputRequest;
import com.bsi.pusbin.modules.input.schema.InputResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InputService {
    private final InputRepository repository;

    public InputPageResponse<InputResponse> getPaginatedList(String search, int page, int size) {
        int offset = page * size;
        List<InputResponse> data = repository.findAll(search, size, offset);
        long totalElements = repository.countAll(search);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        InputPageResponse<InputResponse> res = new InputPageResponse<>();
        res.setContent(data);
        res.setPage(page);
        res.setSize(size);
        res.setTotalElements(totalElements);
        res.setTotalPages(totalPages);
        return res;
    }

    public InputResponse getDetail(Long id) {
        return repository.findById(id);
    }

    public void save(InputRequest req) {
        // Resolve string values to IDs via ImportRepository logic or simple lookup
        // To save time, we will just delegate to repository to handle string lookup & insert
        repository.save(req);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public Map<String, List<Map<String, String>>> getFormOptions() {
        return new HashMap<>(); // Empty for now, can be populated if needed
    }
}
