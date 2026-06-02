package com.bsi.pusbin.modules.table;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/table")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @GetMapping({"/wilayah-kerja"})
    public ResponseEntity<APIResponse<List<List<Object>>>> getWilayahKerjaTable(@ModelAttribute FilterRequest request) {
        List<List<Object>> data = tableService.getWilayahKerjaTable(request);
        return ResponseEntity.ok(APIResponse.ok(data));
    }

    @GetMapping({"/jabatan"})
    public ResponseEntity<APIResponse<List<List<Object>>>> getJabatanTable(@ModelAttribute FilterRequest request) {
        List<List<Object>> data = tableService.getJabatanTable(request);
        return ResponseEntity.ok(APIResponse.ok(data));
    }

    @GetMapping({"/pendidikan"})
    public ResponseEntity<APIResponse<List<List<Object>>>> getPendidikanTable(@ModelAttribute FilterRequest request) {
        List<List<Object>> data = tableService.getPendidikanTable(request);
        return ResponseEntity.ok(APIResponse.ok(data));
    }

    @GetMapping({"/instansi"})
    public ResponseEntity<APIResponse<List<List<Object>>>> getInstansiTable(@ModelAttribute FilterRequest request) {
        List<List<Object>> data = tableService.getInstansiTable(request);
        return ResponseEntity.ok(APIResponse.ok(data));
    }
}
