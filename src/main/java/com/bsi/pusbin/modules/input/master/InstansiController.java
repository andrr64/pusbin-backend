package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.InstansiDto;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master/instansi")
@RequiredArgsConstructor
public class InstansiController {
    private final InstansiService service;

    @GetMapping
    public ResponseEntity<APIResponse<List<InstansiDto>>> findAll() {
        return ResponseEntity.ok(APIResponse.ok(service.findAll(), "Success"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<InstansiDto>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(APIResponse.ok(service.findById(id), "Success"));
    }

    @PostMapping
    public ResponseEntity<APIResponse<InstansiDto>> create(@RequestBody InstansiDto dto) {
        return ResponseEntity.ok(APIResponse.ok(service.create(dto), "Created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<InstansiDto>> update(@PathVariable Integer id, @RequestBody InstansiDto dto) {
        return ResponseEntity.ok(APIResponse.ok(service.update(id, dto), "Updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(APIResponse.ok(null, "Deleted"));
    }
}
