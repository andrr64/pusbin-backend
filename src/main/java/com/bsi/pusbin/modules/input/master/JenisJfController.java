package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.JenisJfDto;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master/jenis-jf")
@RequiredArgsConstructor
public class JenisJfController {
    private final JenisJfService service;

    @GetMapping
    public ResponseEntity<APIResponse<List<JenisJfDto>>> findAll() {
        return ResponseEntity.ok(APIResponse.ok(service.findAll(), "Success"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<JenisJfDto>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(APIResponse.ok(service.findById(id), "Success"));
    }

    @PostMapping
    public ResponseEntity<APIResponse<JenisJfDto>> create(@RequestBody JenisJfDto dto) {
        return ResponseEntity.ok(APIResponse.ok(service.create(dto), "Created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<JenisJfDto>> update(@PathVariable Integer id, @RequestBody JenisJfDto dto) {
        return ResponseEntity.ok(APIResponse.ok(service.update(id, dto), "Updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(APIResponse.ok(null, "Deleted"));
    }
}
