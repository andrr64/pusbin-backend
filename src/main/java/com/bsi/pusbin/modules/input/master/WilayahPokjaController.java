package com.bsi.pusbin.modules.input.master;

import com.bsi.pusbin.modules.input.master.schema.WilayahPokjaDto;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master/wilayah-pokja")
@RequiredArgsConstructor
public class WilayahPokjaController {
    private final WilayahPokjaService service;

    @GetMapping
    public ResponseEntity<APIResponse<List<WilayahPokjaDto>>> findAll() {
        return ResponseEntity.ok(APIResponse.ok(service.findAll(), "Success"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<WilayahPokjaDto>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(APIResponse.ok(service.findById(id), "Success"));
    }

    @PostMapping
    public ResponseEntity<APIResponse<WilayahPokjaDto>> create(@RequestBody WilayahPokjaDto dto) {
        return ResponseEntity.ok(APIResponse.ok(service.create(dto), "Created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<WilayahPokjaDto>> update(@PathVariable Integer id, @RequestBody WilayahPokjaDto dto) {
        return ResponseEntity.ok(APIResponse.ok(service.update(id, dto), "Updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(APIResponse.ok(null, "Deleted"));
    }
}
