package com.bsi.pusbin.modules.input;

import com.bsi.pusbin.modules.input.schema.InputPageResponse;
import com.bsi.pusbin.modules.input.schema.InputRequest;
import com.bsi.pusbin.modules.input.schema.InputResponse;
import com.bsi.pusbin.modules.input.schema.SyncRequest;
import com.bsi.pusbin.shared.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/input")
@RequiredArgsConstructor
public class InputController {

    private final InputService inputService;

    /**
     * Endpoint to fetch paginated ASN list with optional search query.
     */
    @GetMapping
    public ResponseEntity<APIResponse<InputPageResponse<InputResponse>>> getPaginatedList(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        InputPageResponse<InputResponse> response = inputService.getPaginatedList(search, page, size);
        return ResponseEntity.ok(APIResponse.ok(response, "Data ASN berhasil ditampilkan"));
    }

    /**
     * Endpoint to fetch detailed ASN data by ID.
     */
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<APIResponse<InputResponse>> getDetail(@PathVariable Long id) {
        InputResponse response = inputService.getDetail(id);
        return ResponseEntity.ok(APIResponse.ok(response, "Detail ASN berhasil ditampilkan"));
    }

    /**
     * Endpoint to save (insert or update) manual ASN data.
     */
    @PostMapping
    public ResponseEntity<APIResponse<Void>> save(@RequestBody @Valid InputRequest request) {
        inputService.save(request);
        return ResponseEntity.ok(APIResponse.ok(null, "Data ASN berhasil disimpan"));
    }

    /**
     * Endpoint to delete manual ASN data by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Long id) {
        inputService.delete(id);
        return ResponseEntity.ok(APIResponse.ok(null, "Data ASN berhasil dihapus"));
    }

    /**
     * Endpoint for bulk synchronization (insert, update, and delete in batch).
     */
    @PostMapping("/sync")
    public ResponseEntity<APIResponse<Void>> sync(@RequestBody SyncRequest syncReq) {
        inputService.sync(syncReq);
        return ResponseEntity.ok(APIResponse.ok(null, "Proses sinkronisasi data berhasil dijalankan"));
    }
}
