package com.bsi.pusbin.modules.input;

import com.bsi.pusbin.modules.input.schema.InputPageResponse;
import com.bsi.pusbin.modules.input.schema.InputRequest;
import com.bsi.pusbin.modules.input.schema.InputResponse;
import com.bsi.pusbin.shared.response.APIResponse;
import com.bsi.pusbin.shared.security.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@Auth
@RestController
@RequestMapping("/api/v1/input")
@RequiredArgsConstructor
public class InputController {
    private final InputService service;

    @GetMapping
    public ResponseEntity<APIResponse<InputPageResponse<InputResponse>>> getPaginatedList(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(APIResponse.ok(service.getPaginatedList(search, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<InputResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(APIResponse.ok(service.getDetail(id)));
    }

    @PostMapping
    public ResponseEntity<APIResponse<Void>> save(@Valid @RequestBody InputRequest request) {
        service.save(request);
        return ResponseEntity.ok(APIResponse.ok(null, "Created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> update(@PathVariable Long id, @Valid @RequestBody InputRequest request) {
        service.update(id, request);
        return ResponseEntity.ok(APIResponse.ok(null, "Updated"));
    }

    @DeleteMapping("/all")
    public ResponseEntity<APIResponse<Void>> deleteAll() {
        service.deleteAll();
        return ResponseEntity.ok(APIResponse.ok(null, "Deleted All"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(APIResponse.ok(null, "Deleted"));
    }

    @GetMapping("/options")
    public ResponseEntity<APIResponse<Map<String, List<Map<String, String>>>>> getFormOptions() {
        return ResponseEntity.ok(APIResponse.ok(service.getFormOptions()));
    }
}
