package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.TotalAsnPeriodeDto;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master/total-asn-periode-by-nama-jabatan")
@RequiredArgsConstructor
public class TotalAsnPeriodeController {
    private final TotalAsnPeriodeService service;

    @GetMapping
    public ResponseEntity<APIResponse<List<TotalAsnPeriodeDto>>> findAll() {
        return ResponseEntity.ok(APIResponse.ok(service.findAll(), "Success"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<TotalAsnPeriodeDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(APIResponse.ok(service.findById(id), "Success"));
    }

    @PostMapping
    public ResponseEntity<APIResponse<TotalAsnPeriodeDto>> create(@RequestBody TotalAsnPeriodeDto dto) {
        return ResponseEntity.ok(APIResponse.ok(service.create(dto), "Created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<TotalAsnPeriodeDto>> update(@PathVariable Long id, @RequestBody TotalAsnPeriodeDto dto) {
        return ResponseEntity.ok(APIResponse.ok(service.update(id, dto), "Updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(APIResponse.ok(null, "Deleted"));
    }
}
